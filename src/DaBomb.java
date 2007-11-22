import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.util.*;
import java.io.*;

class ImageScreen extends Canvas {
  private Image image;
  
  public ImageScreen() {
    setFullScreenMode(false);
  }
  
  public void setImage(Image i) {
    image = i;
    repaint();
  }
  
  protected void paint(Graphics g) {
    int h = getHeight();
    int w = getWidth();
    
    g.setColor(0xffffff);
    g.fillRect(0, 0, w, h);
    if (image != null) {
      g.drawImage(image, w/2, h/2, Graphics.HCENTER | Graphics.VCENTER);
    }
  }
}

public class DaBomb extends MIDlet implements CommandListener {
  static public String APP_TITLE = "da BOMB" ;

  private Command exitCommand; 
  private Command startCommand;
  private Command stopCommand;
  private Command okCommand;
  private Form main;
  private int frames;
  private Timer timer;
  private Player tickPlayer;
  private Player boomPlayer;
  private Display display;
  private Image tick;
  private Image tack;
  private Image boom;

  public DaBomb() throws IOException {
    display = Display.getDisplay(this);
    startCommand = new Command("Start",Command.OK, 2);
    stopCommand = new Command("Stop",Command.OK, 2);
    exitCommand = new Command("Exit", Command.EXIT, 1);
    okCommand = new Command("Ok", Command.OK, 1);
    
    tick = Image.createImage("/tick.png");
    tack = Image.createImage("/tack.png");
    boom = Image.createImage("/boom.png");
  }

  public void startApp() {
    main = new Form(APP_TITLE);
    main.append("Press START and the bomb will explode after " +
                "a random time between 10 seconds and one minute!");
    main.addCommand(startCommand);
    main.addCommand(exitCommand);
    main.setCommandListener(this);
    display.setCurrent(main);
  }

  public void pauseApp() { }

  public void destroyApp(boolean unconditional) {
    if (timer != null) timer.cancel();
    if (tickPlayer != null) tickPlayer.close();
    if (boomPlayer != null) tickPlayer.close();
  }

  public void commandAction(Command c, Displayable s) {
    if (c == exitCommand) {
      destroyApp(false);
      notifyDestroyed();
    } else if (c == stopCommand || c == Alert.DISMISS_COMMAND) {
      if (timer != null) timer.cancel();
      if (tickPlayer != null) tickPlayer.close();
      if (boomPlayer != null) boomPlayer.close();
      display.setCurrent(main);
    } else if (c == okCommand) {
      display.setCurrent(main);
    } else if (c == startCommand) {
      final ImageScreen screen = new ImageScreen();
      screen.addCommand(stopCommand);
      screen.setCommandListener(this);

      // 10 bis 60 sekunden...
      // (bei 2fps a.k.a. 500ms delay)
      int minsecs = 10;
      int maxsecs = 50;
      int fps = 4;
      frames = (new Random()).nextInt((maxsecs-minsecs)*fps) + minsecs*fps;
      
      VolumeControl vc;
      Control cs[];
      try {
        tickPlayer = Manager.createPlayer(getClass().getResourceAsStream("/ticktack.wav"), "audio/x-wav");  
        boomPlayer = Manager.createPlayer(getClass().getResourceAsStream("/boom.wav"), "audio/x-wav");
        tickPlayer.setLoopCount(-1);
        tickPlayer.start();
        boomPlayer.prefetch();
        cs = tickPlayer.getControls();
        for (int i = 0; i < cs.length; i++) {
          if (cs[i] instanceof VolumeControl) {
            vc = (VolumeControl)cs[i];
            vc.setLevel(100);
          }
         }
      } catch(Exception e) {}
      screen.setImage(tick);
      display.setCurrent(screen);
      timer = new Timer();
      timer.scheduleAtFixedRate(new TimerTask() {
        public final void run() {
          if (frames-- <= 0) {
            timer.cancel();
            try {
              tickPlayer.stop();
              tickPlayer.close();
              boomPlayer.start();
              Control cs[] = boomPlayer.getControls();
              if (cs instanceof Control[]) for (int i = 0; i < cs.length; i++) {
                if (cs[i] instanceof VolumeControl)
                  ((VolumeControl)cs[i]).setLevel(100);
               }
            } catch(MediaException e) {}
            screen.removeCommand(stopCommand);
            screen.addCommand(okCommand);
            screen.setImage(boom);
            display.vibrate(3000);
            return;
          }
          screen.setImage(frames % 2 == 0 ? tick : tack);
        }
      }, 0, 1000/fps);
      }
        }
      }