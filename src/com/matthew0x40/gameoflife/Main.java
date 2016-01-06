package com.matthew0x40.gameoflife;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
 
public class Main extends Applet {
	private static final long serialVersionUID = -4283626609594445178L;
	private static Main instance;
    private boolean running = true;
    
    public static final String title = "Game of Life"; 
    public JFrame frame;
    public Game game;
     
    // FPS Fields
    private int frameCount;
    private int fps;
    private int fps_cap = 64;
    private final int NANO_IN_SEC = 1000000000;
    private final int NANO_IN_MILLIS = 1000000;
    private long frameNanoseconds = NANO_IN_SEC / fps_cap;
    
    // Options panel
    public JPanel optionsPanel;
    JTextField genTextField;
    JButton pauseButton;
    JButton clearButton;
    JSlider speedSlider;
    
    public Main() {
        instance = this;
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1200, 800));
        frame.add(this, BorderLayout.WEST);
        frame.setResizable(false);
        
        try {
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {
	    	// Oh well
	    }
        
        initGUI();
        
        setPreferredSize(new Dimension(800, 800));
    }
     
    @Override
    public void start() {
        game = new Game(this);
        addMouseListener(game);
        addMouseMotionListener(game);
        addKeyListener(game);
        
        frame.pack();
        frame.setVisible(true);
        
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fps = frameCount;
                frameCount = 0;
            }
        }, 0, 1000);
        while(running) {
            long startTime = System.nanoTime();
            // Get screen graphics
            Graphics2D g = (Graphics2D) this.getGraphics();
             
            // Create image and Graphics2D
            BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            applyQualityRendering(g2d);
             
            // Update and Render
            game.update(image.getWidth(), image.getHeight());
            game.render(g2d, image.getWidth(), image.getHeight());
             
            // Render image to screen
            g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
             
            // Dispose stuff
            g.dispose();
            g2d.dispose();
            image.flush();
             
            // Increment frame count
            frameCount++;
             
            // Fps cap stuff
            long timeTaken = System.nanoTime() - startTime;
            if (timeTaken < frameNanoseconds) {
                long sleepMillis = (frameNanoseconds - timeTaken) / (NANO_IN_MILLIS);
                int sleepNanoExtra = (int) (((double) frameNanoseconds - (double) timeTaken) % ((double) NANO_IN_MILLIS));
                try {
                    Thread.sleep(sleepMillis, sleepNanoExtra);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
         
        System.exit(0);
    }
    
    public static void applyQualityRendering(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
     
    @Override
    public void stop() {
        running = false;
    }
     
    public boolean isRunning() {
        return running;
    }
     
    public static Main getInstance() {
        return instance;
    }
     
    public static void main(String[] args) {
        new Main().start();
    }
 
    public int getFps() {
        return fps;
    }
     
    public int getFpsCap() {
        return fps_cap;
    }
     
    public void setFpsCap(int cap) {
        this.fps_cap = cap;
        this.frameNanoseconds = NANO_IN_SEC / fps_cap;
    }
    
    private void initGUI() {
    	optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setPreferredSize(new Dimension(395, 800));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(optionsPanel, BorderLayout.EAST);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.togglePaused();
			}
        	
        });
        optionsPanel.add(pauseButton, gbc);
        
        gbc.gridx = 1;
        clearButton = new JButton("Clear All");
        clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				game.initGrid(game.grid);
			}
        	
        });
        optionsPanel.add(clearButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 2;

        JLabel speedSliderLabel = new JLabel("<html><p><b>Adjust delay between gens</b> (millis)</p></html>");
        speedSliderLabel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
        optionsPanel.add(speedSliderLabel, gbc);

        gbc.gridy = 2;
        speedSlider = new JSlider(JSlider.HORIZONTAL,
                0, 2000, 125);
        speedSlider.setMajorTickSpacing(500);
        speedSlider.setMinorTickSpacing(125);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);

        optionsPanel.add(speedSlider, gbc);

        gbc.gridy = 3;
        JLabel instructions = new JLabel("<html><p>You can draw by left clicking on the game panel and erase by right clicking.<br/><br/><b>Generations:</b></p></html>");
        instructions.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
        optionsPanel.add(instructions, gbc);

        gbc.gridy = 4;
        genTextField = new JTextField();
        optionsPanel.add(genTextField, gbc);
        
        gbc.gridy = 5;
        gbc.weighty = 1;
        optionsPanel.add(new JPanel(), gbc);
    }
}