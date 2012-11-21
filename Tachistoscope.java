import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import com.apple.eawt.*;
import java.awt.Image;
import java.util.concurrent.*;
import java.net.URL;
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
	private int flashInterval = 700;
	private ActionListener startButtonAction;
	private ActionListener stopButtonAction;
    private JButton startButton;
	private JButton stopButton;
	private JButton resetButton;
    private ActionListener resetButtonAction;
	private ActionListener delayAction;
	private ActionListener loadTextAction;
	private ScheduledExecutorService scheduler;
	private JMenuItem setVisibilityDurationMenuItem;
	JMenuItem setFlashIntervalMenuItem;
	private int visibilityDuration = 100;
	private JMenuItem loadTextMenuItem;
	private ActionListener setVisibilityDurationAction;
	private final Dimension preferredDimension= new Dimension(500, 100);
	public static void main(String[] args) throws FileNotFoundException
	{
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", APPTITLE);
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		Application application = Application.getApplication();
		URL imageURL = Tachistoscope.class.getResource("resources/icon.png");
		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
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
    public void displayLine(int lineNum){
		flashPanel.remove(flashLabel);
		flashLabel = new JLabel(lines.get(lineNum).text);
		flashLabel.setFont(new Font("Serif", Font.PLAIN, 24));
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
                words.add(in.next());
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
            if (maxLineLength > preferredDimension.width)
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
		startButton = new JButton("start");
		stopButton = new JButton("stop");
		resetButton = new JButton("reset");
        controlPanel.add(startButton);
		controlPanel.add(stopButton);
		controlPanel.add(resetButton);
        mainPanel.add(flashPanel);
		flashLabel = new JLabel("text will flash here");
		flashLabel.setFont(new Font("Serif", Font.PLAIN, 24));
		flashPanel.add(flashLabel);
		flashPanel.setPreferredSize(preferredDimension);
		flashPanel.setMaximumSize(flashPanel.getPreferredSize()); 
		flashPanel.setMinimumSize(flashPanel.getPreferredSize());
		mainPanel.add(controlPanel);
		controlWindow.add(mainPanel);
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		loadTextMenuItem = new JMenuItem("Load Text");
		fileMenu.add(loadTextMenuItem);
		JMenu menu = new JMenu("Customize");
		menuBar.add(fileMenu);
		menuBar.add(menu);
		setFlashIntervalMenuItem = new JMenuItem("set flash interval");
		setVisibilityDurationMenuItem = new JMenuItem("set visibility duration");
		menu.add(setFlashIntervalMenuItem);
		menu.add(setVisibilityDurationMenuItem);
		controlWindow.setJMenuBar(menuBar);
		controlWindow.pack();
        controlWindow.setVisible(true);
		bindLoadTextButton();
        bindSetFlashIntervalMenuItem();
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
	public void unbindSetFlashIntervalMenuItem() {
		setFlashIntervalMenuItem.removeActionListener(delayAction);
		setVisibilityDurationMenuItem.removeActionListener(setVisibilityDurationAction); 
	}
	public void bindSetFlashIntervalMenuItem(){
		delayAction = new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
            	setFlashInterval();
            }
	    };
	    setFlashIntervalMenuItem.addActionListener(delayAction); 
	    setVisibilityDurationAction = new ActionListener() {
			public void actionPerformed(ActionEvent e)
            {
            	setVisibilityDuration();
            }
	    };
	    setVisibilityDurationMenuItem.addActionListener(setVisibilityDurationAction); 
	}
	public void setVisibilityDuration(){
	    JFrame jframe = new JFrame();
			String s = (String)JOptionPane.showInputDialog(
			                    jframe,
			                    "Set the visibility duration in milliseconds.",
			                    "Set Visibility Duration",
			                    JOptionPane.PLAIN_MESSAGE,
			                    null,
			                    null,
			                    this.visibilityDuration);
            if ((s != null) && (s.length() > 0)) {
				try{
					int visibilityDuration = Integer.parseInt(s);
			
			        if (visibilityDuration <= 0)
			        	throw new IllegalArgumentException("Input must be a positive integer.");
			        else if (visibilityDuration > flashInterval)
			        	throw new IllegalArgumentException("Input must be less than or equal to the flash interval.");
			        else
			        	this.visibilityDuration = visibilityDuration;
			        
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
					    e.getMessage() + " No change was made.");
				}
			} else {
				JFrame errorDialog = new JFrame();
				JOptionPane.showMessageDialog(errorDialog,
				    "No change was made.");
			}
	}
	public void setFlashInterval(){
        JFrame jframe = new JFrame();
		//Object[] possibilities = {"200","300","400","500","600","700","800","900","1000"};
		String s = (String)JOptionPane.showInputDialog(
		                    jframe,
		                    "Set the duration in milliseconds of each flash.",
		                    "Set Flash Interval",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    this.flashInterval);
        if ((s != null) && (s.length() > 0)) {
            try{
				int flashDelay = Integer.parseInt(s);
		
		        if (flashDelay <= 0)
		        	throw new IllegalArgumentException();
		        else
		        	this.flashInterval = flashDelay;
		        
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
			JFrame errorDialog = new JFrame();
			JOptionPane.showMessageDialog(errorDialog,
			    "No change was made.");
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
		loadTextMenuItem.addActionListener(loadTextAction);    
	}
	public void unbindLoadTextButton() {
		loadTextMenuItem.removeActionListener(loadTextAction);
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
		unbindSetFlashIntervalMenuItem();
		unbindLoadTextButton();
		bindStopButton();
		final Runnable beeper = new Runnable() {
           public void run() { flash(); }
        };
        final Runnable clearLineRunnable = new Runnable(){
        	public void run() { clearLine();}
        };
        scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(beeper, (long)50, (long)this.flashInterval, TimeUnit.MILLISECONDS);
        if (visibilityDuration < flashInterval)
            scheduler.scheduleAtFixedRate(clearLineRunnable, (long)(visibilityDuration +50), (long)this.flashInterval, TimeUnit.MILLISECONDS);
	}
	public void stopFlash(){
		unbindStopButton();
		scheduler.shutdown();
		//timer.stop();
		bindStartButton();
		bindResetButton();
		bindSetFlashIntervalMenuItem();
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
