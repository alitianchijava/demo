package com.gjm.distributedlinkstatistics.uint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gjm.distributedlinkstatistics.entiy.LinkData;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: FileTestGroup
 * @description: TODO
 * @author: gjm
 * @date: 2020-05-17 17 23
 **/
public class FileTestGroup {

	private  final static String inputFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/trace1.data";
	private  final static String outputFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/result";

	private  final static String testFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/test200.data";
	@JsonIgnore
	private List<LinkData> items;

	public static void main(String[] args) {
		//C:\Users\xiaoguo\Music\gitwork\2020\filetest
		//largeFileIO(testFile,outputFile);
		largeFileIO(inputFile,outputFile);
	}

	// 当逐行读写大于2G的文本文件时推荐使用以下代码
	public static void largeFileIO(String inputFile, String outputFile) {
		try {
			int i=0;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
			//FileWriter fw = new FileWriter(outputFile);
			List<LinkData> linkDataList = new ArrayList<>();
			while (in.ready()) {
				String line = in.readLine();

				//	获取前2W行数据
					String[] strings = line.split("\\|");
					if(strings.length ==9){
						LinkData linkData = new LinkData(strings);
						linkDataList.add(linkData);
					}else {
						System.out.println("strings = "+line);
					}
				i++;

			}

			//最后2W条数据分组自测
			//分组
			Map<String, List<LinkData>> groupBySex = linkDataList.stream().collect(Collectors.groupingBy(LinkData::getTraceId));
			Map<String, List<LinkData>> result = new HashMap<>();
			List<String> liststr = new ArrayList<>();
			groupBySex.forEach( (k,v) ->{
				List<LinkData> list = v;

				list.forEach( ld -> {
					String tags = ld.getTags();
					if((tags.contains("http.status_code") && !tags.contains("http.status_code=200"))
							|| tags.contains("error=1")){
						System.out.println("k="+k);
						System.out.println("tags="+tags);
						liststr.add(k);
						return; //找到目标获取ID后直接跳出本次循环
					}
				});
				/*list.sort(Comparator.comparing(LinkData::getSpanId));
				String tags = list.get(list.size()-1).getTags();
				System.out.println("tags="+tags);
				System.out.println("spanId="+list.get(list.size()-1).getSpanId());
				if((tags.contains("http.status_code") && !tags.contains("http.status_code=200"))
						|| tags.contains("error=1")){
					System.out.println("k="+k);

				}*/
			});
			System.out.println("一共有x="+liststr.size()+"个文件");
			if(liststr.size()>0){
				for(String str : liststr){
					FileWriter fw = new FileWriter(outputFile+"/"+str+".txt");
					List<LinkData> linkDataList1 = groupBySex.get(str);
					linkDataList1.sort(Comparator.comparing(LinkData::getStartTime));
					linkDataList1.forEach(v ->{
						try {
							fw.append(v.toString()+"\r\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					fw.flush();
					fw.close();
				}
			}
			in.close();
			/*fw.flush();
			fw.close();*/
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}

