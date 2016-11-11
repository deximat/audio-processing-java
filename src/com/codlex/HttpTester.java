package com.codlex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.core.Main;

public class HttpTester {
	
	public static void track(int x, int y) {
		
	}
	
	public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException {
		for (int i = 0; i < 10000; i++) {
			int x = i;
			int y = (int) ((Math.sin((double) (x / 10.0)) + Math.sin((double) (x / 20.0))) * 1000);
			 new URL("http://dchart.ddns.net/graph/damjan/points/add/x=" + x + ",y=" + y + ",z=3/").openConnection().getInputStream().close();
			// new URL("http://localhost:8080/graph/test/points/add/x="+x+",y="+y+",z=3").openConnection().getInputStream().close();

			Thread.sleep(200);
		}
	}
}
