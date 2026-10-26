# Triangle
Triangle is Pascal-like programming language defined by Watt & Brown in "Programming Language Processors in Java". This archive contains the Triangle compiler, the Triangle Abstract Machine as well as an assembler and disassembler tool.

## Version
Triangle 2.1-esa6

## Prerequisites
* Java 1.8 compatible VM

## Usage
This directory contains scripts to run the tools (on Windows, use the `.bat` variants).

### Invoke the compiler
	$ ./triangle Examples/count.tri
	(this generates a TAM object file named Examples/count.tam)

### Run the executable

	$ ./tam Examples/count.tam 

### Disassemble an object file

	$ ./tam-dis Examples/count.tam > Examples/count.asm
	
### Assemble TAM assembler code

	$ ./tam-as Examples/count.asm Examples/count.tam

## Build instructions

To recompile after modifying the sources, do: 

	$ mkdir bin
	$ cd src ; javac -d ../bin \
		Triangle/Compiler.java \
		Triangle/SyntacticAnalyzer/Token.java \
		TAM/Interpreter.java \
		TAM/Disassembler.java \
		TAM/Assembler.java

## Credits
The code was originally written by

	D.A. Watt and D.F. Brown
	Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
	and School of Computer and Math Sciences, The Robert Gordon University,
	St. Andrew Street, Aberdeen AB25 1HG, Scotland.

and

	Matthijs Bomhoff and Theo Ruys
	Univerity of Twente, 
	Department of Computer Science, 
	Formal Methods & Tools Group,
	Enschede, The Netherlands.

and has been modified to
* support file I/O,
* increase the initial memory limits, and
* adhere to modern Java design practices

by members of the Embedded Systems and Applications Group (ESA) at TU Darmstadt.
