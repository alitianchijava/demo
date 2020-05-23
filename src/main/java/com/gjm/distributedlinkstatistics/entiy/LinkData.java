package com.gjm.distributedlinkstatistics.entiy;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: LinkData
 * @description: TODO
 * @author: gjm
 * @date: 2020-05-16 21 10
 **/

@Data
public class LinkData implements Serializable {

	String traceId;//全局唯一的Id，用作整个链路的唯一标识与组装
	String startTime;//调用的开始时间
	String spanId;// 调用链中某条数据(span)的id
	String parentSpanId;// 调用链中某条数据(span)的父亲id，头节点的span的parantSpanId为0
	String duration;//调用耗时
	String serviceName;//调用的服务名
	String spanName;//调用的埋点名
	String host;//机器标识，比如ip，机器名
	String tags;//链路信息中tag信息，存在多个tag的key和value信息。格式为key1=val1&key2=val2&key3=val3 比如 http.status_code=200&error=1

	public LinkData(String[] strings){
		this.traceId =strings[0];
		startTime =strings[1];
		spanId=strings[2];
		parentSpanId=strings[3];
		duration=strings[4];
		serviceName=strings[5];
		spanName=strings[6];
		host=strings[7];
		tags=strings[8];
	}

	public LinkData() {
	}

	@Override public String toString() {
		return "traceId=" + traceId  + "| startTime="
				+ startTime  + "| spanId=" + spanId
				+ "| parentSpanId=" + parentSpanId +  "| duration="
				+ duration +"| serviceName=" + serviceName
				+ "| spanName=" + spanName  + "| host=" + host
				+ "| tags=" + tags ;
	}
	//private List<LinkData> children;
/*	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<ResponseOrderItem> children;

	@JsonIgnore
	private List<ResponseOrderItem> items;

	@JsonIgnore
	public List<ResponseOrderItem> getItems() {
		return items;
	}

	@JsonProperty
	public void setItems(List<ResponseOrderItem> items) {
		this.items = items;
	}*/
/*	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public String getParentSpanId() {
		return parentSpanId;
	}

	public void setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getSpanName() {
		return spanName;
	}

	public void setSpanName(String spanName) {
		this.spanName = spanName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}*/
}
