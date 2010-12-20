#include <SPI.h>
#include <MCP4351.h>

/*
Simple example using the MCP4351 library.  The only difference
between MCP.init_dbg(baud) and MCP.init() is that .init_debug
has a Serial.begin(baud) as the first line.  In the future it
will be a significantly different function, but for now it 
really doesn't matter which you use.

Wiper channels:
MCP_CH0
MCP_CH1
MCP_CH2
MCP_ch3

Debugging functions (queries MCP4351 and prints formatted response)
MCP.stat_ch()    Reads wiper position on all 4 channels (in binary)
MCP.stat_ch0..2()    Reads individual wiper position (in binary)...
    will eventually be replaced by stat_ch(channel)
MCP.dump()       Reads all registers in the MCP's EEPROM and prints
                    the results.  Refer to documentation for meaning
MCP.stat_dump()    Reads off the status registers (TCON0,TCON1, STAT)
MCP.incr(channel)    steps the channel's wiper up 
MCP.decr(channel)    steps the channel's wiper down

Refer to documentation for more...
*/




void setup(){
    MCP.init_dbg(9600);
    Serial.begin(9600);
    MCP.dump();
    Serial.println("\n\n");
    MCP.stat_ch();
    delay(10);
    MCP.setwiper(MCP_CH2,50);
    delay(10);
    MCP.stat_ch();
}

/*just type a letter from below in the serial port
and the MCP will adjust the wiper to the corresponding
selection and read back the location from the
wiper channel to confirm its adjustment*/
void loop(){
    char comm = Serial.read();
    if(comm == 'a'){
        MCP.setwiper(MCP_CH2,50);
        MCP.stat_ch2();
    }
    if(comm == 's'){ 
        MCP.setwiper(MCP_CH2,150);
        MCP.stat_ch2();
    }
    if(comm == 'd'){ 
        MCP.setwiper(MCP_CH2,250);
        MCP.stat_ch2();
    }
    if(comm == 'z'){ 
        MCP.setwiper(MCP_CH1,50);
        MCP.stat_ch1();
    }
    if(comm == 'x'){ 
        MCP.setwiper(MCP_CH1,150);
        MCP.stat_ch1();
    }
    if(comm == 'c'){ 
        MCP.setwiper(MCP_CH1,250);    
        MCP.stat_ch1();
    }
    if(comm == 'q'){ 
        MCP.setwiper(MCP_CH0,50);
        MCP.stat_ch0();
    }
    if(comm == 'w'){ 
        MCP.setwiper(MCP_CH0,150);
        MCP.stat_ch0();
    }
    if(comm == 'e'){ 
        MCP.setwiper(MCP_CH0,250);        
        MCP.stat_ch0();
    }
    if(comm == 'A'){
        MCP.stat_ch();
    }

}






