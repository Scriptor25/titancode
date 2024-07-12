# TitanCode

## About

TitanCode (wip title) is a simple toy programming language run by an interpreter based on Java. It has a highly functional character, e.g. in contrast to the fully object oriented Java language. It's currently in the making, so don't expect too much. If you don't understand this documentation because I have written it very poorly, just take a look at the examples in the [resources](src/main/resources/) directory.

## Features

 - no types
 - no semicolons or other line delimiters
 - comments: ```# ... #```
 - define variables: ```def <name> = <expression>```
 - define functions: ```def <name>(<arg>..., ?) = <expression>```
 - function calls: ```<name>(<arg>...)```
 - use of native Java functions: ```native("<name>", <arg>...)```
 - use variadic args:
    - get variadic array: ```?```
    - get by index: ```?{<index>}```
 - if: ```if <condition> <expression> else <expression>```
 - while: ```while <condition> <expression>```
 - range: ```[<from>, <to>]{<var>} <expression>```, ```{<var>}``` is optional
 - if, while, range and functions return the last evaluated expressions value

## WIP

 - control flow: switch
 - file includes
 - early return, break, continue
 - char "type"
 - real arrays and objects
