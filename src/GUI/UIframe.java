package GUI;
import Crawler.crawler;
import Checker.nlpchecker;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public class UIframe {
	public static void main(String[] args) {
		Frame f=new Frame("LanguageCorrection");
		

		Label l1 = new Label("waiting for an url to start crawlering");
		TextField tf1=new TextField(90);
		tf1.setText("input an url to start");
		Button b1=new Button("Run Crawler");
		TextField tf2=new TextField(95);
		tf2.setText("input a sentence");
		Button b2=new Button("Check");
		TextArea ta=new TextArea(20,100);
		f.setBounds(400, 300, 800, 600);

		

		//button for input url
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s1=tf1.getText().trim();
				l1.setText("crawlering starts from  " + s1);
				f.setBounds(400, 300, 800, 600);
				File file1 = null;
				FileWriter fw = null;
				try {
					file1 = new File("url.txt");
					file1.createNewFile();
					fw = new FileWriter(file1);
					fw.write(s1);
					fw.flush();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				String[] inputurl = "a".split(" ");
				inputurl[0] = "url.txt";
				try {
					C.main(inputurl);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				tf1.setText("input an url to start");
			}
		});
		
		
		
		
		//button for input sentence
		b2.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String s2=tf2.getText().trim();
				l1.setText("Checking  " + s2);
				f.setBounds(400, 300, 800, 600);
				File file2 = null;
				FileWriter fw = null;
				try {
					file2 = new File("sentence.txt");
					file2.createNewFile();
					fw = new FileWriter(file2);
					fw.write(s2);
					fw.flush();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				String[] inputsentence = "a".split(" ");
				inputsentence[0] = "sentence.txt";
				
				try {
					nlpchecker checker = new nlpchecker();
					checker.json1 = new JSONObject();
					checker.main(inputsentence);
					ta.setText("");
					String[] strings = checker.json1.toJSONString().split(",");
					for(int i = 0; i < strings.length; i++) {
						ta.append(strings[i]+"\n");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				tf1.setText("input a sentence");
				

			}
		});
				

		f.add(l1);
		f.add(tf1);
		f.add(b1);
		f.add(tf2);
		f.add(b2);
		f.add(ta);
		f.setLayout(new FlowLayout());
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		f.setVisible(true);
		
		
	}
	private static crawler C = new crawler();
}
