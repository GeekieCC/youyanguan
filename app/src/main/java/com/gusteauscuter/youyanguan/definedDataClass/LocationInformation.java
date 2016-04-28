package com.gusteauscuter.youyanguan.definedDataClass;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

public class LocationInformation {
	private String location;
	private String detailLocation;
	private String searchNum;
	private String barcode;
	private String volume;
	private String status;
	
	public LocationInformation(Element tr) {
		Elements elements = tr.getElementsByTag("td");
		location = elements.get(0).text();
		detailLocation = elements.get(1).text();
		searchNum = elements.get(2).text();
		barcode = elements.get(3).text();
		volume = elements.get(4).text();
		status = elements.get(5).text();
				
	}
	
	public String toString() {
		return location + "||" + detailLocation
				+ "\n" + searchNum + "||" + barcode + "||" + volume + "||" + status;
				
	}
	
	public String getLocation() {
		return optimizeLocation(location);
	}
	public String getDetailLocation() {
		return optimizeLocation(detailLocation);
	}

	private String optimizeLocation(String s) {
		String split = "（";
		int position = s.indexOf(split);
		if (position == -1) {
			return s;
		}
		return s.substring(0, position).trim() + "\n" + s.substring(position).trim();
	}
	public String getSearchNum() {
		return searchNum;
	}
	public String getBarcode() {
		return barcode;
	}
	public String getVolume() {
		return volume;
	}
	public String getStatus() {
		return status;
	}

	public static int checkBorrowCondition(List<LocationInformation> locationLists) {
		boolean south = false;
		boolean north = false;
		for (LocationInformation locInfo : locationLists) {
			String location = locInfo.getLocation();
			String detailLocation = locInfo.getDetailLocation();
			String status = locInfo.getStatus();
			if (!location.contains("停") && !detailLocation.contains("停") && status.contains("在馆")) {
				if ((location.contains("北") || detailLocation.contains("北"))) {
					north = true;
				} else if ((location.contains("南") || detailLocation.contains("南"))) {
					south = true;
				}
			}
		}
		if (!south && !north) return BaseBook.BOTH_NOT;
		if (south && north) return BaseBook.BOTH_YES;
		if (south && !north) return BaseBook.SOUTH_ONLY;
		return BaseBook.NORTH_ONLY;
	}
	
}
