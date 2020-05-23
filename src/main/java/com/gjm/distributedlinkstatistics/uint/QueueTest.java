package com.gjm.distributedlinkstatistics.uint;

import com.gjm.distributedlinkstatistics.entiy.LinkData;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: QueueTest
 * @description: 队列处理
 * @author: gjm
 * @date: 2020-05-17 20 54
 **/
public class QueueTest {

	private  final static String inputFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/trace1.data";
	private  final static String outputFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/queueresult";

	private  final static String testFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/test200.data";

	public static void main(String[] args) throws Exception{

		//largeFileIO(inputFile);
		largeFileIO(testFile);
	}

	public static void largeFileIO(String inputFile) throws Exception{
		try {
			int cecheNum=0;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
			List<LinkData> errorList = new ArrayList<>();
			LinkedList<LinkData> queue = new LinkedList<>();//声明队列
			while (in.ready()) {
				System.out.println("num="+cecheNum);
				String line = in.readLine();

				if(queue.size()==20){
					//把这次循环的行 放到队列的最后一位
					String[] strings = line.split("\\|");
					if(strings.length ==9){
						LinkData linkData = new LinkData(strings);
						queue.offer(linkData);
					}else {
						System.out.println("strings = "+line);
					}
					//取第一个元素遍历
					LinkData first =queue.getFirst();
					//判断第一个元素是否是之前符合条件的元素（因为每次只删除了一个元素）
					if(errorList !=null && errorList.contains(first)){
						queue.removeFirst();
						System.out.println("size ="+queue.size());
						return;
					}
					//如果第一个元素的同类中有符合条件的数据，记录符合条件的traceId
					LinkData test = new LinkData();
					//循环
					queue.forEach(v ->{
							if(first.getTraceId().equals(v.getTraceId())){
								String tags = v.getTags();
								if((tags.contains("http.status_code") && !tags.contains("http.status_code=200"))
										|| tags.contains("error=1")){
									System.out.println("k="+v.getTraceId());
									System.out.println("tags="+tags);

									test.setTraceId(v.getTraceId());
									return; //找到目标获取ID后直接跳出本次循环
								}
							}
							}
					);
					//获取所有符合条件的LinkData
					if(!StringUtils.isEmpty(test.getTraceId())){
						//通过errorList中的traceId 重新遍历，获取所有一条链的list
						queue.forEach(v ->{
									if(test.getTraceId().equals(v.getTraceId())){
										errorList.add(v);
									}
								}
						);
					}
					//移除队列的第一个元素(不管成功失败第一个元素都算处理过了，移除它)
					queue.removeFirst();
					System.out.println("size ="+queue.size());

				}else {
					//	获取前2W行数据
					String[] strings = line.split("\\|");
					if(strings.length ==9){
						LinkData linkData = new LinkData(strings);
						queue.offer(linkData);
					}else {
						System.out.println("非发数据结构 = "+line);
					}
				}

				cecheNum++;

			}
			//2W之前的所有的数据处理
			//writeFile(errorList);
			//最后2W条数据处理
			writeFile(queue);

			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}


	private static void writeFile(List<LinkData> queue){
		//最后2W条数据分组自测
		Map<String, List<LinkData>> groupBySex = queue.stream().collect(Collectors.groupingBy(LinkData::getTraceId));
		List<String> liststr = new ArrayList<>();

		groupBySex.forEach( (k,v) -> {
			List<LinkData> list = v;
			list.forEach(ld -> {
				String tags = ld.getTags();
				if ((tags.contains("http.status_code") && !tags.contains("http.status_code=200"))
						|| tags.contains("error=1")) {
					System.out.println("tags=" + tags);
					liststr.add(k);
					return; //找到目标获取ID后直接跳出本次循环
				}
			});
		});

		System.out.println("最后2W条一共有x=" + liststr.size() + "个文件");
		/*if (liststr.size() > 0) {
			for (String str : liststr) {
				FileWriter fw = null;
				try {

				fw = new FileWriter(outputFile + "/" + str + ".txt");
				List<LinkData> linkDataList1 = groupBySex.get(str);
				linkDataList1.sort(Comparator.comparing(LinkData::getStartTime));
				linkDataList1.forEach(sv -> {
					fw.append(sv.toString() + "\r\n");

				});
				fw.flush();
				fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}*/
	}
}
