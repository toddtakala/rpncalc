<img align="right" width="150" src="../Images/Configure.png">

# Configuration
There are a few, although not many, configuration options available in RPNCalc.  These are settings which control various aspects of the program and are persistent between executions.  When these are changed, they are saved in the Java Preferences system.

There are currently three configuration options that can be changed the commands to do so are listed below.  They are:

#### Program Width
Program Width controls the amount of characters the base program uses.  This can be changed and is especially useful if using a terminal with a small number of characters per line.

#### Memory Slots
By default there are 10 memory slots numbers 0 through 9.  This is probably more than most people would need, but if you save a lot of values, or have a User Defined Function that needs to store a lot of numbers, it can be changed.  Each memory slot is simply an additional array element, so I don't think there is much of a memory impact, but I suppose if you have thousands it would increase memory usage.

#### Display Alignment
By default, the numbers displayed on the stack are left aligned.  There are times when it is prefered to be right aligned (for example if you are working with money and always want two decimal places), or decimal aligned which is nice to easily see the integer from the decimal.  Play around with it and see which one you like for difference circumstances.


#### Reset
Ok, this isn't really a configuration option, but the command can be used to reset everything back to the defaults.  At the time of this writing, the defaults are:
- Program Width:  `80`  Current minimum width is `46` characters
- Memory Slots: `10`  Numbered `0` through `9`
- Display Alignment: `l(eft)`

|<div style="width:110px">Command</div>|Description  |
|-------|-------------|
|reset| This command resets the configuration setting that are set with the `set` command back to their default values|
|set|Display the current values of the configurable persistent settings|
|set width `NUM`| Sets the width of the program.  If you are using a small display, and the calculator wraps, this can be used to make the width smaller (or larger).  Please note that there is a minimum width that must be used. This setting is persistent across RPNCalc executions|
|set mem `NUM`| Set the number of memory slots available to RPNCalc to `NUM`.  If you need more, or less, it can be changed with this command.  The setting is persistent across RPNCalc executions.  `set memslots` or `set memoryslots` may also be used.  See the memory commands chapter for more information|
|set align `l`<br><br>set align `d`<br><br>set align `r`| Set the alignment of the stack when it's displayed<br><br>`l` or left alignment aligns on the left of the number<br>`r` or right alignment has the numbers aligned to the right<BR>`d` or decimal aligns all of the decimal points together in a column<br><br>This setting is persistent across RPNCalc executions. `set alignment` may also be used| 