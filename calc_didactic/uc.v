`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company:
// Engineer:
//
// Create Date:    20:14:45 11/26/2011
// Design Name:
// Module Name:    uc
// Project Name:
// Target Devices:
// Tool versions:
// Description:
//
// Dependencies:
//
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
//
//////////////////////////////////////////////////////////////////////////////////
module uc(
        clk,
        rst,
        ri,
        ind,
        regs_addr,
        regs_oe,
        regs_we,
        alu_oe,
        alu_carry,
        alu_opcode,
        ram_oe,
        ram_we,
        io_oe,
        io_we,
        cp_oe,
        cp_we,
        ind_sel,
        ind_oe,
        ind_we,
        am_oe,
        am_we,
        aie_oe,
        aie_we,
        t1_oe,
        t1_we,
        t2_oe,
        t2_we,
        ri_oe,
        ri_we,
        disp_state
    );

parameter word_width =          16;
parameter state_width =         16;

`define ADC                     0
`define SBB1                    1
`define SBB2                    2
`define NOT                     3
`define AND                     4
`define OR                      5
`define XOR                     6
`define SHL                     7
`define SHR                     8
`define SAR                     9

`define RA                      0
`define RB                      1
`define RC                      2
`define IS                      3
`define XA                      4
`define XB                      5
`define BA                      6
`define BB                      7

input                           clk;
input                           rst;
input [word_width-1 : 0]        ri;
input [word_width-1 : 0]        ind;
output reg                      alu_oe;
output reg                      alu_carry;
output reg[3 : 0]               alu_opcode;
output reg                      ram_oe;
output reg                      ram_we;
output reg                      io_oe;
output reg                      io_we;
output reg[2 : 0]               regs_addr;
output reg                      regs_oe;
output reg                      regs_we;
output reg                      cp_oe;
output reg                      cp_we;
output reg                      ind_sel;        // controls IND register input (0 = bus, 1 = alu flags)
output reg                      ind_oe;
output reg                      ind_we;
output reg                      am_oe;
output reg                      am_we;
output reg                      aie_oe;
output reg                      aie_we;
output reg                      t1_oe;
output reg                      t1_we;
output reg                      t2_oe;
output reg                      t2_we;
output reg                      ri_oe;          // controls RI register output which generates the offset for Jcond instructions
output reg                      ri_we;
output[state_width-1 : 0]       disp_state;

wire [0:6]                      cop;
wire                            d;
wire [0:1]                      mod;
wire [0:2]                      rg;
wire [0:2]                      rm;
wire [0:3]							  jump_val;

assign jump_val = {ri[4], ri[5], ri[6], ri[7]};

assign cop  = {ri[0], ri[1], ri[2], ri[3], ri[4], ri[5], ri[6]};
assign d    = {ri[7]};
assign mod  = {ri[8], ri[9]};
assign rg   = {ri[10], ri[11], ri[12]};
assign rm   = {ri[13], ri[14], ri[15]};

`define reset                   'h00            // reset state
`define fetch                   'h10            // load instruction to instruction register
`define decode                  'h20            // analyze loaded instruction
`define addr_sum                'h30            // computes address of the form [By+Xz] with y,z in {A, B}
`define addr_reg                'h34            // computes address of the form [yz] with y in {X, B} and z in {A, B}
`define load_src_reg            'h40            // load source operand from register
`define load_src_mem            'h44            // load source operand from memory
`define load_dst_reg            'h50            // load destination operand from register
`define load_dst_mem            'h54            // load destination operand from memory
`define exec_1op                'h60            // execute 1 operand instructions
`define exec_2op                'h64            // execute 2 operand instructions
`define store_reg               'h70            // store result to register
`define store_mem               'h74            // store result to memory
`define inc_cp                  'h80            // increment program counter
`define load_depls				  'h90				// load depls
`define add_depls					  'h100 				// add depls
`define load_instant				  'h110				// load imediate op
`define push						  'h120				// push
`define pop    					  'h130				// pop
`define decoded_jmp				  'h140				// jump_cond
`define pushf						  'h150				// pushf
`define popf						  'h160				// popf
`define load_depl_01				  'h170				// load 01 mode deplasament
`define call						  'h180				// call

reg [state_width-1 : 0] state = `reset, state_next;
reg [state_width-1 : 0] decoded_src, decoded_src_next;      // stores decoded source operand load state
reg [state_width-1 : 0] decoded_dst, decoded_dst_next;      // stores decoded destination operand load state
reg [state_width-1 : 0] decoded_exec, decoded_exec_next;    // stores decoded execute state
reg [state_width-1 : 0] decoded_store, decoded_store_next;  // stores decoded store state
reg decoded_d, decoded_d_next;                              // stores decoded direction bit

// FSM - sequential part
always @(posedge clk) begin
    state <= `reset;

    if(!rst) begin
        state <= state_next;

        if(state == `decode) begin
            decoded_src <= decoded_src_next;
            decoded_dst <= decoded_dst_next;
            decoded_exec <= decoded_exec_next;
            decoded_store <= decoded_store_next;
            decoded_d <= decoded_d_next;
        end
    end
end

// FSM - combinational part
always @(*) begin
    state_next = `reset;
    decoded_src_next = `reset;
    decoded_dst_next = `reset;
    decoded_exec_next = `reset;
    decoded_store_next = `reset;
    decoded_d_next = 0;
    alu_oe = 0;
    alu_carry = 0;
    alu_opcode = 0;
    ram_oe = 0;
    ram_we = 0;
    io_oe = 0;
    io_we = 0;
    regs_addr = 0;
    regs_oe = 0;
    regs_we = 0;
    cp_oe = 0;
    cp_we = 0;
    ind_sel = 0;
    ind_oe = 0;
    ind_we = 0;
    am_oe = 0;
    am_we = 0;
    aie_oe = 0;
    aie_we = 0;
    t1_oe = 0;
    t1_we = 0;
    t2_oe = 0;
    t2_we = 0;
    ri_oe = 0;
    ri_we = 0;

    case(state)
        `reset: begin
            state_next = `fetch;
        end

        `fetch: begin
            cp_oe = 1;
            am_we = 1;

            state_next = `fetch + 1;
        end

        `fetch + 'd1: begin
            am_oe = 1;

            state_next = `fetch + 2;
        end

        `fetch + 'd2: begin
            ram_oe = 1;
            ri_we = 1;

            state_next = `decode;
        end

        `decode: begin
            // decode location of operands and operation
				
				if(cop[1] == 0)begin		// 1 op
					if(cop[3] == 0) begin	//transfer 
						if(cop[0] == 0) begin	//cu adresa efectiva
							if(cop[2] == 0) begin	// fara operand imediat
								
								if(cop[5] == 1)begin				//push/pop
									decoded_d_next      = d;
									decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;
									decoded_exec_next   = `exec_1op;
									decoded_src_next    = (cop[0:6] == 7'b0000011 && mod != 2'b11) ? `pop : ((mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem);
									decoded_store_next  = (mod == 2'b11) || (d == 1) ? `store_reg : `store_mem;
								end
								
								else begin							//mov/jmp
									decoded_d_next      = d;
									decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;
									decoded_exec_next   = `exec_1op;
									decoded_src_next    = (cop[4:6]== 3'b101 && mod != 2'b11) ? `exec_1op : ((mod == 2'b11) || (d == 0) ? `load_src_reg : `load_src_mem);
									decoded_store_next  =  cop[4:6]== 3'b101 ? `fetch : ((mod == 2'b11) || (d == 1) ? `store_reg : `store_mem);
								end
								
							end
							
							if(cop[2] == 1) begin	// cu operand imediat
								
								if(cop[5] == 1)begin				//push/pop
									decoded_d_next      = d;
									decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;
									decoded_exec_next   = `exec_1op;
									decoded_src_next    = `load_instant;
									decoded_store_next  = (mod == 2'b11) || (d == 1) ? `store_reg : `store_mem;
								end
								
								else begin							//mov/jmp
									decoded_d_next      = d;
									decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;
									decoded_exec_next   = `exec_1op;
									decoded_src_next    = `load_instant;
									decoded_store_next  =  cop[4:6]== 3'b101 ? `fetch : ((mod == 2'b11) || (d == 1) ? `store_reg : `store_mem);
								end
								
							end
						end
						if(cop[0] == 1) begin	//fara o adresa efectiva
									decoded_d_next      = d;
									decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;
									decoded_exec_next   = `exec_1op;
									decoded_src_next    = (cop[0:6] == 7'b1000011 && mod != 2'b11) ? `popf : ((mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem);
									decoded_store_next  = `inc_cp;
						end
					end
					if(cop[3] == 1) begin	//operatii
						if(cop[0] == 0) begin	//cu adresa efectiva
							if(cop[2] == 0) begin	// fara operand imediat
								
								 decoded_d_next      = 0;
								 decoded_dst_next    = mod == 2'b11 ? `load_dst_reg : `load_dst_mem;
								 decoded_src_next    = mod == 2'b11 ? `load_src_reg : `load_src_mem;
								 decoded_exec_next   = `exec_1op;
								 decoded_store_next  = mod == 2'b11 ? `store_reg : `store_mem; 
								
							end
						end
						if(cop[0] == 1) begin	//fara adresa efectiva
							if(cop[2] == 0) begin	// fara operand imediat
								
								decoded_src_next	  = `exec_1op;
								
							end
						end
					end
				end
				
				if(cop[1] == 1)begin		// 2 op
					if(cop[3] == 0) begin	// nu salveaza rezultatul
						if(cop[0] == 0) begin	//cu adresa efectiva
							if(cop[2] == 0) begin	// fara operand imediat
								
								decoded_d_next      = d;
								decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;
								decoded_src_next    = (mod == 2'b11) || (d == 0) ? `load_src_reg : `load_src_mem;
								decoded_exec_next   = `exec_2op;
								decoded_store_next  = `inc_cp ; 
								
							end
							if(cop[2] == 1) begin	// cu operand imediat
								
								decoded_d_next      = d;
								decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem; 
								decoded_src_next    = `load_instant;
								decoded_exec_next   = `exec_2op;
								decoded_store_next  = `inc_cp ;
								
							end
						end
					end
					if(cop[3] == 1) begin	// salveaza rezultatul
						if(cop[0] == 0) begin	//cu adresa efectiva
							if(cop[2] == 0) begin	// fara operand imediat
								
								decoded_d_next      = d;
								decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem;	 
								decoded_src_next    = (mod == 2'b11) || (d == 0) ? `load_src_reg : `load_src_mem;
								decoded_exec_next   = `exec_2op;
								decoded_store_next  = (mod == 2'b11) || (d == 1) ? `store_reg : `store_mem;	 
								
							end
							if(cop[2] == 1) begin	// cu operand imediat
								
								decoded_d_next      = d;
								decoded_dst_next    = (mod == 2'b11) || (d == 1) ? `load_dst_reg : `load_dst_mem; 
								decoded_src_next    = `load_instant;
								decoded_exec_next   = `exec_2op;
								decoded_store_next  = (mod == 2'b11) || (d == 1) ? `store_reg : `store_mem;
								
							end
						end
					end
				end			
				
            // decode address calculation mode
					case(mod)
						 2'b00: begin
							  state_next = rm[0] ? `addr_reg : `addr_sum;
						 end
						 
						 2'b11: begin
							  state_next = decoded_src_next;

						 end
						 
						 2'b10: begin
							  state_next = `load_depls;

						 end
						 
						 2'b01: begin
								state_next = rm[0:1] == 2'b11 ?  `load_depl_01 : `addr_sum;
						 end
					endcase
        end
		  
		  `call:begin
				cp_oe = 1;
				t2_we = 1;
				
				state_next = `call + 1;
		  end
		  
		  `call + 'd1:begin
				t2_oe = 1;
				alu_opcode = `ADC;
				alu_carry = 1;
				alu_oe = 1;
				cp_we = 1;
				
				state_next = `call + 2;
		  end
		  
		  `call + 'd2:begin
				regs_addr = `IS;
				regs_oe = 1;
				t2_we = 1;
				
				state_next = `call + 3;
		  end
		  
		  `call + 'd3:begin
				t2_oe = 1;
				alu_opcode = `SBB2;
				alu_carry = 1;
				alu_oe = 1;
				am_we = 1;
				regs_addr = `IS;
				regs_we = 1;
				
				state_next = `call + 4;
			end
			
			`call + 'd4:begin
				am_oe = 1;
				
				state_next = `call + 5;
			end
			
			`call + 'd5:begin
				ram_we = 1;
				cp_oe = 1;
				
				state_next = `call + 6;
			end
			
			`call + 'd6:begin
				state_next = `call + 7;
			end
			
			`call + 'd7:begin
				t1_oe = 1;
				alu_opcode = `OR;
				alu_oe = 1;
				cp_we = 1;
				
				state_next = `fetch;
			end
		  
		  `load_depl_01:begin
				cp_oe = 1;
				t1_we = 1;
				
				state_next = `load_depl_01 + 1;
		  end
		  
		  `load_depl_01 + 'd1:begin
				t1_oe = 1;
				alu_opcode = `ADC;
				alu_carry = 1;
				
				alu_oe = 1;
				cp_we = 1;
				
				am_we = 1;
		  
				state_next = `load_depl_01 + 2;
		  end
		  
		  `load_depl_01 + 'd2:begin
				am_oe = 1;
				
				state_next = rm[0:2] == 3'b111 ? `load_depl_01 + 3 : `load_depl_01 + 6;
		  end
		  
		  `load_depl_01 + 'd3:begin
				ram_oe = 1;
				t2_we = 1;
				
				state_next = `load_depl_01 + 4; 
		  end
		  
		  `load_depl_01 + 'd4:begin
				t2_oe = 1;
					
				alu_opcode = `OR;
				alu_oe = 1;
				
				am_we = 1;
				
				state_next = `load_depl_01 + 5;
		  end
		  
		  `load_depl_01 + 'd5:begin
				am_oe = 1;
				
				state_next = `load_depl_01 + 6;
		  end
		  
		  `load_depl_01 + 'd6:begin
				ram_oe = 1;
				if(decoded_d)
					t2_we = 1;
				else
					t1_we = 1;
				state_next = decoded_d == 1 ? decoded_dst : decoded_src;
		  end
		  
		  `pushf:begin
				regs_addr = `IS;
				regs_oe = 1;
				t2_we = 1;
				state_next = `pushf + 1;
		  end
		  
		  `pushf + 'd1:begin
				t1_oe = 0;
				t2_oe = 1;
				alu_opcode = `SBB2;
				alu_carry = 1;
				alu_oe = 1;
				
				regs_addr = `IS;
				regs_we = 1;
				
				t2_we = 1;
				
				am_we = 1;
				
				state_next = `pushf + 2;
		  end
		  
		  `pushf +'d2:begin
				am_oe = 1;
				
				ind_oe = 1;
				ram_we = 1;
				state_next = `push + 3;
		  end
		  
		  `push:begin
				regs_addr = `IS;
				regs_oe = 1;
				t2_we = 1;
				state_next = `push + 1;
		  end
		  
		  `push + 'd1:begin
				t1_oe = 0;
				t2_oe = 1;
				alu_opcode = `SBB2;
				alu_carry = 1;
				alu_oe = 1;
				
				regs_addr = `IS;
				regs_we = 1;
				
				t2_we = 1;
				
				am_we = 1;
				
				state_next = `push + 2;
		  end
		  
		  `push +'d2:begin
				am_oe = 1;
				
				t1_oe = 1;
				t2_oe = 0;
				alu_opcode = `OR;
				alu_oe = 1;
				ram_we = 1;
				state_next = `push + 3;
		  end
		  
		  `push+'d3:begin
				state_next = `inc_cp;
		  end
		  
		  `popf:begin
				regs_addr = `IS;
				regs_oe = 1;
				am_we = 1;
				
				t2_we = 1;
				state_next = `popf +1;
			end	
		  
		  `popf + 'd1:begin
				t2_oe = 1;
				alu_opcode = `ADC;
				alu_carry = 1;
				alu_oe = 1;
				
				regs_addr = `IS;
				regs_we = 1;
				
				am_oe = 1;
		
				state_next = `popf+2;
			end
		  
		  `popf+ 'd2:begin
				ram_oe = 1;
				ind_we = 1;
				
				state_next = decoded_store;
			end
		  
		  `pop:begin
				regs_addr = `IS;
				regs_oe = 1;
				am_we = 1;
				t2_we = 1;
			end
			
			`pop + 'd1:begin
				t1_oe = 0;
				t2_oe = 1;
				alu_opcode = `ADC;
				alu_carry = 1;
				alu_oe = 1;
				
				regs_addr = `IS;
				regs_we = 1;
				
				am_oe = 1;
		
				state_next = `pop+2;
			end
			
			`pop+ 'd2:begin
				ram_oe = 1;
				t2_we = 1;
				
				if(mod == 2'b11)
					state_next = `pop+4;
				else
					state_next = `pop+3;
			end
		  
			`pop+ 'd3:begin
			
				t1_oe = 1;
				t2_oe = 0;
				alu_opcode = `OR;
				alu_oe = 1;
				am_we = 1;
				
				state_next = `pop+4;
			end
			
			`pop+ 'd4:begin
				t1_oe = 0;
				t2_oe = 1;
				alu_opcode = `OR;
				alu_oe = 1;
				t1_we = 1;
				
				state_next = decoded_store;
			end
		  
		  `load_depls: begin
				cp_oe = 1;
				t1_we = 1;
				state_next = `load_depls + 1;
		  end
		  
		  `load_depls + 'd1: begin
				t1_oe = 1;
            t2_oe = 0;
            alu_carry = 1;
            alu_opcode = `ADC;
				alu_oe = 1;
				cp_we = 1;
				
				state_next = cop[0:6] == 4 ? `addr_reg : (rm[0] ? `addr_reg : `addr_sum);
		  end
		  
		  `addr_reg: begin
            regs_addr = rm;
            regs_oe = 1;
            if(decoded_d)
                t2_we = 1;
            else
                t1_we = 1;
				
            if(mod == 2'b10)
					state_next = `add_depls;
				else
					if(cop[2] == 1)
						state_next = `load_instant;
					else
						state_next = decoded_src;
        end
        
		  `addr_sum: begin
            regs_addr = rm[1] ? `BB : `BA;
            regs_oe = 1;
            t1_we = 1;

            state_next = `addr_sum + 1;
        end

        `addr_sum + 'd1: begin
            regs_addr = rm[2] ? `XB : `XA;
            regs_oe = 1;
            t2_we = 1;

            state_next = `addr_sum + 2;
        end

        `addr_sum + 'd2: begin
            t1_oe = 1;
            t2_oe = 1;
            alu_carry = 0;
            alu_opcode = `ADC;
            alu_oe = 1;
            if(decoded_d)
                t2_we = 1;
            else
                t1_we = 1;
				
				if(mod == 2'b10)
					state_next = `add_depls;
				else
					if(cop[2] == 1)
						state_next = `load_instant;
					else
						state_next = decoded_src;
        end
        
		  `add_depls: begin
				cp_oe = 1;
            am_we = 1;
				
				state_next = `add_depls + 1;
		  end
		  `add_depls + 'd1: begin
				am_oe = 1;
				state_next = `add_depls + 2;
		  end
		  
		  `add_depls + 'd2: begin
				ram_oe = 1;
				if(decoded_d)
                t1_we = 1;
            else
                t2_we = 1;
				
				state_next = `add_depls + 3;
		  end
		  
		  `add_depls + 'd3: begin
				t1_oe = 1;
            t2_oe = 1;
            alu_carry = 0;
            alu_opcode = `ADC;
            alu_oe = 1;
            if(decoded_d)
                t2_we = 1;
            else
                t1_we = 1;
				if(cop[2] == 1)
					state_next = `load_instant;
				else	
					state_next = decoded_src;
				if(cop[0:6] == 7'b0000100) state_next = `call;
		  end
		  
		 
		  `load_instant: begin
				cp_oe = 1;
				t2_we = 1;
				state_next = `load_instant + 1;
		  end
		  
		  `load_instant + 'd1: begin
				t1_oe = 0;
				t2_oe = 1;
				alu_carry = 1;
            alu_opcode = `ADC;
				alu_oe = 1;
				cp_we = 1;
				state_next = `load_instant + 2;
		  end
		  
		  `load_instant + 'd2: begin
				cp_oe = 1;
				am_we = 1;
				state_next = `load_instant + 3;
		  end
		  
		  `load_instant + 'd3: begin
				am_oe = 1;
				state_next = `load_instant + 4;
		  end
		  
		  `load_instant + 'd4: begin
				ram_oe = 1;
				if(decoded_d)
                t1_we = 1;
            else
                t2_we = 1;
				state_next = decoded_dst;
		  end
		  
		  
        `load_src_reg: begin
            regs_addr = decoded_d ? rm : rg;
            regs_oe = 1;
            t2_we = 1;

            state_next = decoded_dst;
        end
        
        `load_src_mem: begin
            t1_oe = 0;
            t2_oe = 1;
            alu_opcode = `OR;
            alu_oe = 1;
            am_we = 1;

            state_next = `load_src_mem + 1;
        end

        `load_src_mem + 'd1: begin
            am_oe = 1;

            state_next = `load_src_mem + 2;
        end

        `load_src_mem + 'd2: begin
            ram_oe = 1;
            t2_we = 1;

            state_next = decoded_dst;
        end

        `load_dst_reg: begin
            regs_addr = decoded_d ? rg : rm;
            regs_oe = 1;
            t1_we = 1;
				
				if(cop[0:6] == 2)
					state_next = `push;
				else	if(cop[0:6] == 3)
					state_next = `pop;
				else	if(cop[0:6] == 7'b1000010)
					state_next = `pushf;
				else	if(cop[0:6] == 7'b1000011)
					state_next = `popf;
				else
					state_next = decoded_exec;
        end
        
        `load_dst_mem: begin
		  
            t1_oe = 1;
            t2_oe = 0;
            alu_opcode = `OR;
            alu_oe = 1;
            am_we = 1;

            state_next = `load_dst_mem + 1;
        end

        `load_dst_mem + 'd1: begin
            am_oe = 1;

            state_next = `load_dst_mem + 2;
        end

        `load_dst_mem + 'd2: begin
            ram_oe = 1;
            t1_we = 1;
				
				if(cop[0:6] == 2)
					state_next = `push;
				else	if(cop[0:6] == 3)
					state_next = `pop;
				else	if(cop[0:6] == 7'b1000010)
					state_next = `pushf;
				else	if(cop[0:6] == 7'b1000011)
					state_next = `popf;
				else
					state_next = decoded_exec;
        end

        `exec_1op: begin
            
				
					if(cop[0] == 0) begin // operatii
						if(cop[3] == 1) begin
							t1_oe = 1;
							case(cop[4:6])
								 3'b000: begin                               // INC
									  alu_carry = 1;
									  alu_opcode = `ADC;
								 end
								 3'b001: begin                               // DEC
									  alu_carry = 1;
									  alu_opcode = `SBB1;
								 end
								 3'b010: begin                               // NEG
									  alu_carry = 0;
									  alu_opcode = `SBB2;
								 end
								 3'b011: begin                               // NOT
									  alu_opcode = `NOT;
								 end
								 3'b100: alu_opcode = `SHL;                  // SHL/SAL
								 3'b101: alu_opcode = `SHR;                  // SHR
								 3'b110: alu_opcode = `SAR;                  // SAR
							endcase
							ind_sel = 1;
							ind_we = 1;
						end
						else begin
							case(cop[4:6])
								3'b000: begin												// MOV
									t2_oe = 1;
									alu_opcode = `OR;
								end
								3'b101:begin												// JMP
									t1_oe = 1;
									alu_opcode = `OR;
									cp_we = 1;
									
								end
							endcase
						end
						alu_oe = 1;
						t1_we = 1;
						state_next = decoded_store;
					end
					
					
					else begin 
						// cond jump
						case(jump_val)
						
							4'b0000: begin									// JBE
								if( (ind[4] | ind[2]) == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0001: begin									// JB / JC
								if( ind[3] == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0010: begin									// JLE
								if( ((ind[1]^ind[3]) | ind[2]) == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0011: begin									// JL
								if( (ind[1]^ind[3]) == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0100: begin									// JE/JZ
								if( ind[2] == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0101: begin									// JO
								if( ind[1] == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0110: begin									// JS
								if( ind[3] == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b0111: begin									// JPE
								if( ind[4] == 1 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1000: begin									// JA
								if( (ind[4] | ind[2] ) == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1001: begin									// JAE/JNC
								if( ind[0] == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1010: begin									// JG
								if( ((ind[1]^ind[3]) | ind[2]) == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1011: begin									// JGE
								if( (ind[1]^ind[3]) == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1100: begin									// JNE/JNZ
								if( ind[2] == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1101: begin									// JNO
								if( ind[1] == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1110: begin									// JNS
								if( ind[3] == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
							4'b1111: begin									// JPO
								if( ind[4] == 0 )
									state_next = `decoded_jmp;
								else state_next = `inc_cp;
							end
							
						endcase
					end
		
				

					
					
        end
        
        `exec_2op: begin
            t1_oe = 1;
            t2_oe = 1;
            case(cop[4:6])
                3'b000: begin                               // ADD
                    alu_carry = 0;
                    alu_opcode = `ADC;
                end
                3'b001: begin                               // ADC
                    alu_carry = ind[0];
                    alu_opcode = `ADC;
                end
                3'b010: begin                               // SUB/CMP
                    alu_carry = 0;
                    alu_opcode = `SBB1;
                end
                3'b011: begin                               // SBB
                    alu_carry = ind[0];
                    alu_opcode = `SBB1;
                end
                3'b100: alu_opcode = `AND;                  // AND/TEST
                3'b101: alu_opcode = `OR;                   // OR
                3'b110: alu_opcode = `XOR;                  // XOR
            endcase
            alu_oe = 1;
            t1_we = 1;
            ind_sel = 1;
            ind_we = 1;

            state_next = decoded_store;
        end
		  
		  `decoded_jmp:begin
				cp_oe = 1;
				t1_we = 1;
				
				state_next = `decoded_jmp + 1;
		  end
		  
		  `decoded_jmp + 'd1:begin
				ri_oe = 1;
				t2_we = 1;
				
				state_next = `decoded_jmp + 2;
		  end
		  
		  `decoded_jmp + 'd2:begin
				t1_oe = 1;
				t2_oe = 1;
				alu_opcode = `ADC;
				alu_carry = 0;
				alu_oe = 1;
				
				cp_we = 1;
				state_next = `fetch;
		  end
		  
        `store_reg: begin
            t1_oe = 1;
            t2_oe = 0;
            alu_opcode = `OR;
            alu_oe = 1;
            regs_addr = decoded_d ? rg : rm;
            regs_we = 1;

            state_next = `inc_cp;
        end
        
        `store_mem: begin
            t1_oe = 1;
            t2_oe = 0;
            alu_opcode = `OR;
            alu_oe = 1;
            am_oe = 1;
            ram_we = 1;

            state_next = `store_mem + 1;
        end

        `store_mem + 'd1: begin
            state_next = `inc_cp;
        end

        `inc_cp: begin
            cp_oe = 1;
            t1_we = 1;

            state_next = `inc_cp + 1;
        end

        `inc_cp + 'd1: begin
            t1_oe = 1;
            cp_we = 1;
            alu_oe = 1;
            alu_carry = 1;
            alu_opcode = `ADC;

            state_next = `fetch;
        end

        default: ;
    endcase
end

assign disp_state = state;

endmodule