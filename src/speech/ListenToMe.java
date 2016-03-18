/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package speech;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;


public class ListenToMe extends Thread {
    
    private  Microphone microphone;
    private  ConfigurationManager cm = new ConfigurationManager(ListenToMe.class.getResource("myconfig.xml"));    
    private  Recognizer recognizer = (Recognizer)cm.lookup("recognizer");
    private final  String args;
    private boolean status;
    
        
    public ListenToMe(String imgname) {            
        microphone = (Microphone)cm.lookup("microphone");                
        args = imgname;        
    }

    @Override
    public void run (){
        
        setStatus(true);
        
        String voice = "this is a "+args;
        System.out.println(voice);        
        recognizer.allocate();        
        
        if(!microphone.startRecording()){
            System.out.println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

        System.out.println("Say: (This is a | This ) ( Cat | Dog | Giraffe | Butterfly | Giraffe )");
                
        do {
            System.out.println("chk--"+status);
            System.out.println("Start speaking. Press Ctrl-C to quit.\n");
            Result result = recognizer.recognize();
            if(result != null) {
            String resultText = result.getBestFinalResultNoFiller();
            System.out.println((new StringBuilder()).append("You said: ").append(resultText).append('\n').toString());
                
                if(resultText == null ? voice == null : resultText.equals(voice)) {
                                        
                    System.out.println("You got it right!");
                    
                }
            
            } else {
            System.out.println("I can't hear what you said.\n");
               }
                      
           } while(getStatus());
        
    }
    
    private boolean getStatus() {
        return status;
    }
    
    private void setStatus(boolean val) {
        status = val;
    }
           
    public void mstop() {
        setStatus(false);                            
        microphone.stopRecording();   
        recognizer.deallocate();
        Thread.interrupted();
                
    }

}
