

//#include "WProgram.h"
#include "MCP4351.h"
#include <SPI.h>


#define _SS 4
#define _MOSI 5
#define _MISO 6
#define _SCK 7

/*MCP Commands*/
#define    MCP_WRITE		(0x00<<2)   /*16-bit*/
#define    MCP_INCREMENT		(0x1<<2)    /*8-bit*/
#define    MCP_DECREMENT		(0x2<<2)    /*8-bit*/
#define    MCP_READ			(0x3<<2)    /*16-bit*/

/*Wiper addresses*/
#define    MCP_CH0			(0x00<<4)
#define    MCP_CH1			(0x01<<4)
#define    MCP_CH2			(0x06<<4)
#define    MCP_CH3			(0x07<<4)

/*Data addresses*/
#define MCP_DATA_0B			(0xB<<4)
#define MCP_DATA_0C			(0xC<<4)
#define MCP_DATA_0D			(0xD<<4)
#define MCP_DATA_0E			(0xE<<4)
#define MCP_DATA_0F			(0xF<<4)

#define MCP_NVCH0			(0x2<<4)
#define MCP_NVCH1			(0x3<<4)
#define MCP_NVCH2			(0x8<<4)
#define MCP_NVCH3			(0x9<<4)

#define MCP_TCON0			(0x4<<4)
#define MCP_TCON1			(0xA<<4)
#define MCP_STAT			(0x5<<4)

uint8_t MCP_DATADUMP[3] = {
	MCP_STAT, MCP_TCON0, MCP_TCON1};
uint8_t MCP_WIPER[4] = {
	MCP_CH0, MCP_CH1, MCP_CH2, MCP_CH3};
uint8_t MCP_REGS[16] = {
	MCP_CH0,    
	MCP_CH1,    
	MCP_NVCH0,    
	MCP_NVCH1,    
	MCP_TCON0,
	MCP_STAT,
	MCP_CH2,
	MCP_CH3,
	MCP_NVCH2,
	MCP_NVCH3,
	MCP_TCON1,    
	MCP_DATA_0B, 
	MCP_DATA_0C, 
	MCP_DATA_0D, 
	MCP_DATA_0E, 
	MCP_DATA_0F}; 



//constructor
MCPClass MCP;

/*setting configuration to BYTE_MODE for now*/
void MCPClass::init() {
	SPI.begin();
//	DDRA |= (1<<_SS) | (1<<_MOSI) | (1<<_SCK);
//	DDRA &= ~(1<<MISO);
	
	pinMode(_SS,OUTPUT);
	pinMode(_MOSI,OUTPUT);
	pinMode(_MISO,OUTPUT);
	pinMode(_SCK,OUTPUT);
	digitalWrite(_SCK,HIGH);
	digitalWrite(_MISO,HIGH);
	digitalWrite(_MOSI,HIGH);
	digitalWrite(_SS,HIGH);
	
	SPI.transfer(0x00);	
	
	
}

void MCPClass::init_dbg(long baud) {
	Serial.begin(baud);
	SPI.begin();
	pinMode(_SS,OUTPUT);
	pinMode(_MOSI,OUTPUT);
	pinMode(_MISO,OUTPUT);
	pinMode(_SCK,OUTPUT);
	digitalWrite(_SCK,HIGH);
	digitalWrite(_MISO,HIGH);
	digitalWrite(_MOSI,HIGH);
	digitalWrite(_SS,HIGH);
	
	SPI.transfer(0x00);		
}


void MCPClass::stat_ch(){
	int x;
	Serial.println("\n\n================================");
	Serial.println("||    MCP_4351 Wiper Dump     ||");
	Serial.println("================================");      
	for(x = 0; x < 4; x++){
		uint16_t mcp_msg = read(MCP_WIPER[x]);
		uint8_t byte1 = mcp_msg/256;
		uint8_t byte2 = mcp_msg - (byte1*256);
		Serial.print("Wiper ");
		Serial.print(x);
		Serial.print(":\t");
		Serial.println(mcp_msg,BIN);
		delay(25);   
	}
	Serial.println("================================");
}


void MCPClass::stat_ch0(){
	uint16_t mcp_msg = read(MCP_CH0);
	uint8_t byte1 = mcp_msg/256;
	uint8_t byte2 = mcp_msg - (byte1*256);
	Serial.print("Wiper 0:\t");
	Serial.println(mcp_msg,BIN);
	delay(25);        
}

void MCPClass::stat_ch1(){
	uint16_t mcp_msg = read(MCP_CH1);
	uint8_t byte1 = mcp_msg/256;
	uint8_t byte2 = mcp_msg - (byte1*256);
	Serial.print("Wiper 1:\t");
	Serial.println(mcp_msg,BIN);
	delay(25);        
}

void MCPClass::stat_ch2(){
	uint16_t mcp_msg = read(MCP_CH2);
	uint8_t byte1 = mcp_msg/256;
	uint8_t byte2 = mcp_msg - (byte1*256);
	Serial.print("Wiper 2:\t");
	Serial.println(mcp_msg,BIN);
	delay(25);        
}

void MCPClass::stat_ch3(){
	uint16_t mcp_msg = read(MCP_CH3);
	uint8_t byte1 = mcp_msg/256;
	uint8_t byte2 = mcp_msg - (byte1*256);
	Serial.print("Wiper 3:\t");
	Serial.println(mcp_msg,BIN);
	delay(25);        
}



void MCPClass::dump(){
	int i;
	Serial.println("\n\n================================");
	Serial.println("||   MCP_4351 Memory Dump     ||");
	Serial.println("================================");    
	for(i = 0; i < 16; i++){
		uint16_t mcp_msg = read(MCP_REGS[i]);
		uint8_t byte1 = mcp_msg/256;
		uint8_t byte2 = mcp_msg - (byte1*256);
		Serial.print("Register 0x0");
		Serial.print(i,HEX);
		//        Serial.print("\t");
		//        Serial.print(byte1,BIN);
		//        Serial.print(" ");    
		//        Serial.println(byte2,BIN);
		Serial.print("\t");
		Serial.println(mcp_msg,BIN);
		delay(25);
	}
	Serial.println("================================");            
}

void MCPClass::statdump(){
	int i;
	Serial.println("\n\n================================");
	Serial.println("||   MCP_4351 Memory Dump     ||");
	Serial.println("================================");    
	for(i = 0; i < 3; i++){
		uint16_t mcp_msg = read(MCP_DATADUMP[i]);
		uint8_t byte1 = mcp_msg/256;
		uint8_t byte2 = mcp_msg - (byte1*256);
		Serial.print("Register 0x0");
		Serial.print(MCP_DATADUMP[i]/16,HEX);
		Serial.print("\t");
		Serial.print(byte1,BIN);
		Serial.print(" ");    
		Serial.println(byte2,BIN);
		delay(25);
	}
	Serial.println("================================");            
}

/*ex:
 uint8_t response = mcp_read(MCP_CH0);
 */
uint16_t MCPClass::read(uint8_t address){
	uint8_t command = MCP_READ;    
	uint8_t byte1, byte2, data1, response1, response2, response;
	data1 = 0;
	byte2 = 0;
	byte1 = address + command + data1;
	
	slave_on();
	response1 = SPI.transfer(byte1);
	response2 = SPI.transfer(byte2);
	slave_off();
	
	response1 = response1<<8;
	response = response1 + response2;
	return response;
}

/*ex:
 mcp_incr(MCP_CH0,100);
 */
void MCPClass::incr(uint8_t address, uint8_t count){
	int i;
	uint8_t command = MCP_INCREMENT;    
	uint8_t byte1 = address + command;
	//    shiftOut(byte1,LSBFIRST,_SCK,_SS);
	
	slave_on();    
	for(i = 0; i < count; i++){    
		SPI.transfer(byte1);
	}
	slave_off();
}

/*ex:
 mcp_decr(MCP_CH0,100);
 */
void MCPClass::decr(uint8_t address, uint8_t count){
	uint8_t command = MCP_DECREMENT;    
	uint8_t byte1 = address + command;
	//    shiftOut(byte1,LSBFIRST,_SCK,_SS);
	int i;
	slave_on();    
	for(i = 0; i < count; i++){    
		SPI.transfer(byte1);
	}
	slave_off();
}

/*ex:
 mcp_incr(MCP_CH0,VAL,16);
 */
void MCPClass::write(uint8_t address, uint16_t data, uint8_t commsize){
	uint8_t data1, data2, byte1, byte2;
	uint8_t command = MCP_WRITE;
	
	if(commsize == 8){
		data1 = data;
		byte1 = address + command + data;        
		
		slave_on();        
		SPI.transfer(byte1);
	}
	
	else if(commsize == 16){
		
		data1 = data/256;
		data2 = data - (data1*256);
		byte1 = address + command + data1;        
		byte2 = data2;
		
		slave_on();        
		SPI.transfer(byte1);
		SPI.transfer(byte2);
	}
	slave_off();
	
}

/* ex
 mcp_setwiper(MCP_CH0, 127);
 */
void MCPClass::setwiper(uint8_t address, uint16_t val){
	uint8_t data1, data2, byte1, byte2, response, timeout;
	uint8_t command = MCP_WRITE;
	
	data1 = (val/256);
	data2 = val - (data1*256);
	byte1 = address + command + data1;
	byte2 = data2;
	
	slave_on();
	SPI.transfer(byte1);
	SPI.transfer(byte2);
	//    while((response < 255) || (!response)) response = SPI.transfer(...);
	slave_off();
	
}

uint16_t MCPClass::stat(){
	uint8_t command = MCP_READ;
	uint8_t address = MCP_STAT;
	uint8_t byte1, byte2, data1, response1, response2, response;
	data1 = 0;
	byte2 = 0;
	byte1 = address + command + data1;
	slave_on();
	response1 = SPI.transfer(byte1);
	response2 = SPI.transfer(byte2);
	slave_off();
	//    Serial.print("\nResp from 2nd byte:\t");
	//    Serial.println(byte1
	response1 = response1<<8;
	response = response1 + response2;
	return response;
}

uint16_t MCPClass::tcon0(){
	uint8_t command = MCP_TCON0;
	uint8_t address = MCP_STAT;
	uint8_t byte1, byte2, data1, response1, response2, response;
	data1 = 0;
	byte2 = 0;
	byte1 = address + command + data1;
	slave_on();
	response1 = SPI.transfer(byte1);
	response2 = SPI.transfer(byte2);
	slave_off();
	//    Serial.print("\nResp from 2nd byte:\t");
	//    Serial.println(byte1
	response1 = response1<<8;
	response = response1 + response2;
	return response;
}

uint16_t MCPClass::tcon1(){
	uint8_t command = MCP_TCON1;
	uint8_t address = MCP_STAT;
	uint8_t byte1, byte2, data1, response1, response2, response;
	data1 = 0;
	byte2 = 0;
	byte1 = address + command + data1;
	slave_on();
	response1 = SPI.transfer(byte1);
	response2 = SPI.transfer(byte2);
	slave_off();
	//    Serial.print("\nResp from 2nd byte:\t");
	//    Serial.println(byte1
	response1 = response1<<8;
	response = response1 + response2;
	return response;
}



void MCPClass::slave_on(){
	pinMode(_SS,OUTPUT);
	digitalWrite(_SS,LOW);
}    

void MCPClass::slave_off(){
	pinMode(_SS,OUTPUT);
	digitalWrite(_SS,HIGH);
}  	