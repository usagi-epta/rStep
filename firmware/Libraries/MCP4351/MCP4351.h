#ifndef MCP_H
#define MCP_H

#include "WProgram.h"
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

//uint8_t MCP_DATADUMP[3] = {
//	MCP_STAT, MCP_TCON0, MCP_TCON1};
//uint8_t MCP_WIPER[4] = {
//	MCP_CH0, MCP_CH1, MCP_CH2, MCP_CH3};
//uint8_t MCP_REGS[16] = {
//	MCP_CH0,    
//	MCP_CH1,    
//	MCP_NVCH0,    
//	MCP_NVCH1,    
//	MCP_TCON0,
//	MCP_STAT,
//	MCP_CH2,
//	MCP_CH3,
//	MCP_NVCH2,
//	MCP_NVCH3,
//	MCP_TCON1,    
//	MCP_DATA_0B, 
//	MCP_DATA_0C, 
//	MCP_DATA_0D, 
//	MCP_DATA_0E, 
//	MCP_DATA_0F}; 



class MCPClass {
public:
	void init();
	void init_dbg(long baud);
	
	void stat_ch();
	void stat_ch0();
	void stat_ch1();
	void stat_ch2();
	void stat_ch3();
	
	void dump();
	void statdump();
	
	uint16_t read(uint8_t address);
	
	void incr(uint8_t address, uint8_t count);
	void decr(uint8_t address, uint8_t count);
	void write(uint8_t address, uint16_t data, uint8_t commsize);
	void setwiper(uint8_t address, uint16_t val);
	
	uint16_t stat();
	uint16_t tcon0();
	uint16_t tcon1();
	
	
	void slave_on();
	void slave_off();
	
	
	
	
};

extern MCPClass MCP;

#endif
