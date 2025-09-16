# RISC-V 32 Simulator / IDE

## This is a lightweight environment for learning and developing single file RISC-V programs, suitable for simple local editing and testing.

Here are some of the features this development option brings to the table:

- File loading and saving
- Step through debugging
- Live register view with highlighting
- Clean, dark UI
- Built in IO terminal
- Built in error/info terminal
- Compile time line-specific error handling
- Resizable window + sub-windows
- Cross platform functionality
- Simple to use system calls for IO and program interaction

### A Guide to System Calls

To use a system call, you'll load a value into the **`a7` register**, which signifies which system call you are using, followed by using the **ecall instruction**.

Some system calls require input. Such cases will be discussed in each system call's synopsis.

These have been modelled closely after the most important system calls featured in [CPULATOR](https://cpulator.01xz.net/doc/#syscall).

Here are the options:

- Syscall 1: Print Integer
  - Directly interprets the `a0` register's value as a signed integer and prints to terminal.
- Syscall 4: Print String
  - Interprets the `a0` register's value as a memory address from which to start reading from.
  - This action will continue to read bytes until it reaches a null-terimator `('\0')`, then prints to terminal.
- Syscall 5: Read Integer
  - This is a blocking syscall (halts execution flow) - it waits for user input in the terminal.
  - The parsed integer input will be directly loaded into the `a0` register.
- Syscall 8: Read String
  - This is a blocking syscall.
  - Takes 2 inputs - `a0`: address to start reading into, `a1`: maximum number of characters to read.
  - This action will read bytes into memory until either the end of the input string or the maximum characters is reached.
- Syscall 10: Exit
  - Gracefully ends program execution.
- Syscalll 11: Print Character
  - Interprets the `a0` register's value as a character and prints it to the terminal.
- Syscall 12: Read Character
  - This is a blocking syscall.
  - Reads one character input from the terminal and loads it into register `a0`.
- Syscall 30: Load Current Time
 - Loads the number of milliseconds since Jan 1. 1970 into `a0`.

## Requirements

- Java JDK 22 (Will likely work with other versions)
- Maven
