
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import com.apple.eawt.*;
import java.awt.Image;



import javax.swing.*;

class Line 
{
	public String text;
	public int wordLength;
	
	
	public void setText (String text)
	{
		this.text = text;
	}
	public void setWordLength (int wordLength)
	{
		this.wordLength = wordLength;
	}
	
	public Line (String text, int wordLength)
	{
		this.text = text;
		this.wordLength = wordLength;
	}
}


public class Tachistoscope {

	
	
	
	private final static String APPTITLE = "Tachistoscope L1";
	private File textFileToRead;
	int positionInText;
	private ArrayList<Line> lines; 
	private final int preferredLineLength = 25;
	JPanel flashPanel;
	JLabel flashLabel;
	JFrame controlWindow;
	private int flashDelay = 700;
	private ActionListener startButtonAction;
	private ActionListener stopButtonAction;
	private Timer timer;
	private JButton startButton;
	private JButton stopButton;
	private JButton resetButton;
	private JButton delayButton;
	private JButton loadTextButton;
	private ActionListener resetButtonAction;
	private ActionListener delayAction;
	private ActionListener loadTextAction;
	private File textForReading;
	private JButton clipButton;
	private ActionListener clipButtonAction;
	private boolean clip = false;
	
	
	public static void main(String[] args) throws FileNotFoundException
	{
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", APPTITLE);
		Application application = Application.getApplication();
		Image image = Toolkit.getDefaultToolkit().getImage("lib/icon.png");
		application.setDockIconImage(image);
		new Tachistoscope();
	}
	
	
	public void flash() {
		if (positionInText < lines.size()) {
		    displayLine(positionInText);
		    positionInText++;
		} else {
			this.stopFlash();
		}
	}
	
/*
	public void displayLine(int lineNum){
		flashPanel.remove(flashLabel);
		System.out.println("displaying line " + lines.get(lineNum).text);
		flashLabel = new JLabel(lines.get(lineNum).text);
		flashLabel.setFont(new Font("Serif", Font.PLAIN, 24));
		flashPanel.add(flashLabel);
		flashPanel.revalidate();
		controlWindow.repaint();
	}
	*/
	
	public void displayLine(int lineNum){
		flashPanel.remove(flashLabel);
		
		flashLabel = new JLabel(lines.get(lineNum).text);
		flashLabel.setFont(new Font("Serif", Font.PLAIN, 24));
		
		ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                clearLine();
            }
        };
		
		
		Timer clearText = new Timer(500,taskPerformer);
		clearText.start();
		flashPanel.add(flashLabel);
		
		flashPanel.revalidate();
		controlWindow.repaint();
	}
	
	public void clearLine(){
        flashPanel.remove(flashLabel);
        flashPanel.revalidate();
        flashPanel.repaint();
		
	}
	public void bindResetButton(){
			resetButtonAction = new ActionListener() {
				 
	            public void actionPerformed(ActionEvent e)
	            {
	            	reset();
	            }
		    };
		    resetButton.addActionListener(resetButtonAction);
	}
	public void unbindResetButton(){
		resetButton.removeActionListener(resetButtonAction);
	}
	
	public void reset() {
		positionInText = 0;
		this.displayLine(0);
	}
	
	public void parseFileToRead(){


		try {
            Scanner in = new Scanner(textFileToRead);
            

    		
    		ArrayList<String> words = new ArrayList<String>();
            while (in.hasNext())
            {
            	words.add(in.next());
            }
    		int size = words.size();
    		String[] wordArray = new String[size];
            for (int i = 0; i < size; i++) 
                wordArray[i] = words.get(i);	
            String concatLine;
            int i = 0;
            lines = new ArrayList<Line>();
            
            Line newLineObj;
            
            
            while (i < wordArray.length)
            {
                if (wordArray[i].length() >= preferredLineLength) 
    	        {
    	        	newLineObj = new Line(wordArray[i],1);
    	        	i++;
                }
    	        else 
    	        {
    	        	newLineObj = new Line("",0);
    		        
    		        while (newLineObj.wordLength < 4 && i < wordArray.length)
    		        {
    		            concatLine = newLineObj.text + " " + wordArray[i];

    		            if (wordArray[i].equals("and") || wordArray[i].equals("but") || wordArray[i].charAt(wordArray[i].length()-1) == '.') {
    		            	newLineObj.setText(concatLine);
    		            	newLineObj.setWordLength(newLineObj.wordLength+1);
    		            	i++;
    		        		continue;
    	                }
    		            
    		            if (concatLine.length() < preferredLineLength)
    		            {
    		        	    newLineObj.setText(concatLine);
    		                newLineObj.setWordLength(newLineObj.wordLength+1);
    		            } else 
    		            	break;
    		            i++;
    		        }
    	        }
    	        lines.add(newLineObj);
            }
            FontMetrics fm = this.flashLabel.getFontMetrics(new Font("Serif", Font.PLAIN, 24));
            
            int maxLineLength = 0;
            int len;
            for (Line line : lines) {
            	len = fm.stringWidth(line.text);
                if (len > maxLineLength)
                	maxLineLength = len;
            }
            System.out.println("max line length : " + maxLineLength);
            resize(new Dimension(maxLineLength+20, 100));
            
            positionInText = 0;
            displayLine(0);
            bindStartButton();
		}
		catch (FileNotFoundException e) {
			JFrame errorDialog = new JFrame();
			JOptionPane.showMessageDialog(errorDialog,
			    "Could not read the file you specified.");
		}
        catch (Exception e) {
        	JFrame errorDialog = new JFrame();
			JOptionPane.showMessageDialog(errorDialog,
			    "Could not read the file you specified.");
        }
		
	}
	
	public Tachistoscope(){

		controlWindow = new JFrame(APPTITLE);
		controlWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		flashPanel = new JPanel();
		JPanel controlPanel = new JPanel();
		loadTextButton = new JButton("load text");
		
		startButton = new JButton("start");
		stopButton = new JButton("stop");
		resetButton = new JButton("reset");
		delayButton = new JButton("set delay");
		clipButton = new JButton("clip");
		controlPanel.add(loadTextButton);
		controlPanel.add(startButton);
		controlPanel.add(stopButton);
		controlPanel.add(resetButton);
		controlPanel.add(delayButton);
		controlPanel.add(clipButton);
		
		mainPanel.add(flashPanel);
		flashLabel = new JLabel("text will flash here");
		flashLabel.setFont(new Font("Serif", Font.PLAIN, 24));
		flashPanel.add(flashLabel);
		flashPanel.setPreferredSize(new Dimension(500, 100));
		flashPanel.setMaximumSize(flashPanel.getPreferredSize()); 
		flashPanel.setMinimumSize(flashPanel.getPreferredSize());
		mainPanel.add(controlPanel);
		controlWindow.add(mainPanel);
		controlWindow.pack();

		
		controlWindow.setVisible(true);
		
		bindLoadTextButton();
		bindDelayButton();
	
		
	}
	
	public void resize(Dimension d)
	{
		flashPanel.setPreferredSize(d);
		flashPanel.setMaximumSize(flashPanel.getPreferredSize()); 
		flashPanel.setMinimumSize(flashPanel.getPreferredSize());
		flashPanel.revalidate();
		controlWindow.pack();
		controlWindow.repaint();
		
	}
	
	public void unbindDelayButton() {
		delayButton.removeActionListener(delayAction);
	}
	
	public void bindDelayButton(){
		delayAction = new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	setFlashDelay();
            }
	            
	    };
        delayButton.addActionListener(delayAction); 
	}
	
	public void setFlashDelay(){
        JFrame jframe = new JFrame();
		//Object[] possibilities = {"200","300","400","500","600","700","800","900","1000"};
		String s = (String)JOptionPane.showInputDialog(
		                    jframe,
		                    "Set the duration in milliseconds of each flash.",
		                    APPTITLE,
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    this.flashDelay);

		if ((s != null) && (s.length() > 0)) {

			
			try{
				int flashDelay = Integer.parseInt(s);
		
		        if (flashDelay <= 0)
		        	throw new IllegalArgumentException();
		        else
		        	this.flashDelay = flashDelay;
		        
			}
			catch(NumberFormatException e)
			{
				JFrame errorDialog = new JFrame();
				JOptionPane.showMessageDialog(errorDialog,
				    "Input must be a positive integer.  No change was made.");
			}
			catch(IllegalArgumentException e)
			{
				JFrame errorDialog = new JFrame();
				JOptionPane.showMessageDialog(errorDialog,
				    "Input must be a positive integer.  No change was made.");
			}
			
			
		} else {
			flashDelay = 550;
		}
	}
	
	public void bindStartButton(){
		startButtonAction = new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	startFlash();
            }
	            
	    };
		
		
		startButton.addActionListener(startButtonAction);    
		
	}
	
	public void bindLoadTextButton(){
		
		loadTextAction = new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	loadTextFileToRead();
            }
	            
	    };
		
		loadTextButton.addActionListener(loadTextAction);    
		
	}
	
	public void unbindLoadTextButton() {
		loadTextButton.removeActionListener(loadTextAction);
	}
	
	public void unbindStartButton(){
		startButton.removeActionListener(startButtonAction);
	}
	
	public void bindStopButton(){
		stopButtonAction = new ActionListener() {
			 
            public void actionPerformed(ActionEvent e)
            {
            	stopFlash();
            }
	            
	    };
		
		
		stopButton.addActionListener(stopButtonAction);    
		
	}
	
	public void unbindStopButton() {
		
		stopButton.removeActionListener(stopButtonAction);
	}
	
	
	public void startFlash(){
		unbindResetButton();
		unbindStartButton();
		unbindDelayButton();
		unbindLoadTextButton();
		bindStopButton();
		if (timer != null)
		    timer.stop();
		class FlashAction implements ActionListener
	    {
		    private Tachistoscope tachistoscope;
			
			public void actionPerformed(ActionEvent e) {
				tachistoscope.flash();
	        }
			
			public FlashAction(Tachistoscope tachistoscope){
				this.tachistoscope = tachistoscope;
			}
		}
		FlashAction flashAction= new FlashAction(this);
		timer = new Timer(flashDelay,flashAction);
		timer.setRepeats(true);
		
		ActionListener clearAction = new ActionListener() {
		
			public void actionPerformed(ActionEvent e) {
				clearLine();
	        }
        };
		Timer clearTimer = new Timer(flashDelay,clearAction);
		clearTimer.setInitialDelay(flashDelay+100);
		
		
		timer.start();
		clearTimer.start();
		
		
		
	}
	
	public void stopFlash(){
		unbindStopButton();
		
		timer.stop();
		bindStartButton();
		bindResetButton();
		bindDelayButton();
		bindLoadTextButton();
	}
	
	public void loadTextFileToRead() 
	{
	    JFrame loadFileFrame = new JFrame("Choose Text to Read");
	    FileDialog fileDialog = new FileDialog(loadFileFrame,"Choose Text to Read",FileDialog.LOAD);
		fileDialog.setVisible(true);
	    String filePath = fileDialog.getDirectory() + fileDialog.getFile();
		textFileToRead = new File(filePath);
		loadFileFrame.dispose();
		this.parseFileToRead();
		
	}
	
}
