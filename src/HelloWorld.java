import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;


public class HelloWorld extends MIDlet{
  public HelloWorld() {
    Form f = new Form("Form: Hallo Welt");
    Display d = Display.getDisplay(this);

    f.append("Hallo Welt!");

    d.setCurrent(f);
  }

  protected void startApp() throws MIDletStateChangeException{
    new HelloWorld();
  }

  protected void pauseApp(){ }

  protected void destroyApp(boolean unconditional) 
      throws MIDletStateChangeException{ }

}