/*
Example usage for the rstep DBG Library
by Michael Nash
*/


#include <DBG.h>

#define STEP_EN 2
int i;

void setup() {
    /*DBG.begin(long baud) just starts a serial connection.
    it is unnecessary if you've already called Serial.begin*/
    DBG.begin(9600);
    Serial.println("\n\n");    
    
    /*Stepper enable pin is active LOW -- if the pin is
    HIGH then the stepper is disabled*/
    Serial.println("=========================");
    Serial.println("   Function DBG.outAdv   ");
    Serial.println("=========================");    
    DBG.outAdv("Stepper Enable Pin", STEP_EN, "Disabled", "Enabled");
    Serial.println("\n\n");
    
    /*below shows how to make a custom debug function
    for the output direction and state of Stepper
    Reset pin*/
    Serial.println("=========================");
    Serial.println("   Custom debug_rst()   ");
    Serial.println("=========================");    
    debug_rst();
    Serial.println("\n\n");

    /*The function below checks important stepper-function pins. There
    will not be any output if all are set correctly.  This function would
    be handy attached to the user-available interrupt pin eitehr by itself
    or with other custom debugging functions*/
    Serial.println("=========================");
    Serial.println(" Function DBG.steppers() ;");
    Serial.println("=========================");
    DBG.steppers();
    Serial.println("\n\n");

/*the function below checks the currently enabled step-size based
directly on the states of the MS1 and MS2 pins*/
    Serial.println("=========================");
    Serial.println("Function DBG.steppsize();");
    Serial.println("=========================");
    DBG.stepsize();
    Serial.println("\n\n");
    
 /*the function below outputs the state of ALL rstep pins, with
 labels*/
    Serial.println("=========================");
    Serial.println("   Function DBG.all();   ");
    Serial.println("========================="); 
     DBG.all();
     
    
    
}

void loop() {

}

void debug_rst(){
    if(!DBG.checkDir(STEP_RST)) Serial.println("Warning: Stepper Reset pin is set as INPUT");
    else Serial.println("Stepper Reset pin is an OUTPUT");
    
    if(!DBG.check(STEP_RST)) Serial.println("Warning: Stepper Reset pin is LOW");
    else Serial.println("Stepper Reset pin is HIGH");
}


