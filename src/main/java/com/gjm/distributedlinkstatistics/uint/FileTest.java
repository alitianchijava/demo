package com.gjm.distributedlinkstatistics.uint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gjm.distributedlinkstatistics.entiy.LinkData;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: FileTest
 * @description: TODO
 * @author: gjm
 * @date: 2020-05-16 20 43
 **/
public class FileTest {

	private  final static String inputFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/trace1.data";
	private  final static String outputFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/trace1result.txt";

	private  final static String testFile ="C:/Users/xiaoguo/Music/gitwork/2020/filetest/test200.data";
	@JsonIgnore
	private List<LinkData> items;

	public static void main(String[] args) {
		//C:\Users\xiaoguo\Music\gitwork\2020\filetest
		largeFileIO(testFile,outputFile);
	}


	void samorFileIO(String path) throws IOException {

		 path = "C:/Users/xiaoguo/Music/gitwork/2020/filetest/trace1.data";
		RandomAccessFile br = new RandomAccessFile(path, "rw");// 这里rw看你了。要是之都就只写r
		String str = null, app = null;
		int i = 0;
		while ((str = br.readLine()) != null) {
			i++;
			app = app + str;
			if (i >= 100) {// 假设读取100行
				i = 0;
				// 这里你先对这100行操作，然后继续读
				app = null;
			}
			System.out.println("app="+app);
		}
		br.close();
	}
	// 当逐行读写大于2G的文本文件时推荐使用以下代码
	public static void largeFileIO(String inputFile, String outputFile) {
		try {
			int i = 0;
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(inputFile)));
			BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M缓存
			FileWriter fw = new FileWriter(outputFile);
			List<LinkData> linkDataList = new ArrayList<>();
			List<LinkData> linkDataErrorList = new ArrayList<>();
			int sizeOld = linkDataErrorList.size();
			while (in.ready()) {
				System.out.println("linkDataList length="+linkDataList.size());
				String line = in.readLine();
				i++;
				if(i>=21){//逐行处理新的请求
					//由于一个链路的所有请求最多跨2W行，所以第一条链路的结束链路最多在2W+1行
					//
					String[] strNew = line.split("\\|");
					System.out.println("strNew="+line);
					LinkData linkDataNew = null;
					if(strNew.length ==9){
						 linkDataNew = new LinkData(strNew);
					}
					LinkData finalLinkDataNew = linkDataNew;
					String tags = finalLinkDataNew.getTags();
					linkDataList.forEach( v -> {

						if((tags.contains("http.status_code") && !tags.contains("http.status_code=200"))
							|| tags.contains("error=1")){

							if(finalLinkDataNew.getTraceId().equals(v.getTraceId())){

								linkDataErrorList.add(v);
							}
						}
					});

					if(linkDataErrorList.size()>sizeOld){
						sizeOld = linkDataErrorList.size();
						System.out.println("==============="+finalLinkDataNew.getTraceId());
						linkDataList = linkDataList.stream().filter(linkData -> linkData.getTraceId().equals(finalLinkDataNew.getTraceId())).collect(Collectors.toList());
					}

					continue;
				}else {//	获取前2W行数据
					String[] strings = line.split("\\|");
					if(strings.length ==9){
						LinkData linkData = new LinkData(strings);
						linkDataList.add(linkData);
					}else {
						System.out.println("strings = "+line);
						System.out.println("i = "+i);
					}
				}

			}

			//最后2W条数据分组自测
			//分组
			Map<String, List<LinkData>> groupBySex = linkDataList.stream().collect(Collectors.groupingBy(LinkData::getTraceId));
			/*Iterator<String> iterator = groupBySex.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
			}*/
			groupBySex.forEach( (k,v) ->{
				List<LinkData> list = v;
				list.sort(Comparator.comparing(LinkData::getSpanId));
				String tags = list.get(list.size()-1).getTags();
				System.out.println("tags="+tags);
				if((tags.contains("http.status_code") && !tags.contains("http.status_code=200"))
						|| tags.contains("error=1")){
					System.out.println("k="+k);
					linkDataErrorList.add(list.get(list.size()));
				}
			});
			for(int j=0;j<linkDataErrorList.size();j++){
				System.out.println("==========11111111111===============");
				System.out.println(linkDataErrorList.get(j).getTraceId());
			}
			/*Iterator iterator = groupBySex.keySet().iterator();
			while (iterator.hasNext()) {   
			    String key = (String) iterator.next();   
			    if ("1".equals(key) || "2".equals(key)) {   
			       map.remove(key);   
			    }   
			 }*/

			in.close();
			fw.flush();
			fw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

/*	public Map<String, List<LinkData>> getProducts() {
		if (items == null || items.isEmpty()) {
			return Collections.emptyMap();
		}
		var index = items.stream().collect(Collectors.toMap(LinkData::getTraceId, v -> v));
		var result = new HashMap<String, List<LinkData>>(items.size());
		items.forEach(item -> {
			if (String.isNullOrWhitespace(item.getParentSpanId())) {
				// root
				var groupId =
						Strings.isNullOrWhitespace(item.getConfigGroup()) ? NO_GROUP : item.getConfigGroup();
				if (result.containsKey(groupId)) {
					result.get(groupId).add(item);
				} else {
					var root = new ArrayList<ResponseOrderItem>();
					root.add(item);
					result.put(groupId, root);
				}
			} else {
				var fater = index.get(item.getParentId());
				if (fater == null) {
					log.error("[ResponseOrder] data issue: order:{}, id:{} has no partner {}", this.getId(),
							item.getId(), item.getParentId());
					return;
				}
				if (fater.getChildren() == null) {
					fater.setChildren(new ArrayList<ResponseOrderItem>());
				}
				fater.getChildren().add(item);
			}
		});
		return result;
	}*/
}
