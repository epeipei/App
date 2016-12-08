%{
#include<stdio.h>
#include<string.h>
#include<stdbool.h>
int line=1;
bool comment_flag=false;// for /* comment
void func(char *a,char *b){
	printf("if(%s>%s){printf(\"1\");}\n",a,b);
	printf("if(%s<%s){printf(\"-1\");}\n",a,b);
	printf("if(%s==%s){printf(\"0\");}\n",a,b);
}
bool if_1(int line,char *branch){
	printf("\nmycov %d %d %s\n",line,1,branch);
	return true;
}
bool if_0(int line,char *branch){
	printf("\nmycov %d %d %s\n",line,0,branch);
	return false;
}

%}
DIGIT	[0-9]
ID		[a-zA-Z_][a-zA-Z0-9_]*
DOUBLE	{DIGIT}+"."{DIGIT}*
OP		"+"|"-"|"*"|"/"
ROP		>|<|>=|<=|==
INT		{DIGIT}+
DOT		"."
META	{ID}|{INT}|{DOUBLE}
EXP		{META}({OP}{META})*
RCC		{EXP}{ROP}{EXP}
LOP		"&&"|"||"
COND	{RCC}({LOP}{RCC})*
SPACE	[ \t\n]*
%%
\n	{
	line++;
	printf("\n");
	}
#.* { // for include
	printf("%s",yytext);
	}
\".*\" {//for String
	printf("%s",yytext);
	}
\/\/.* {//line comment
	printf("%s",yytext);
	}
\/\* {
	comment_flag=true;
	printf("%s",yytext);
	}
\*\/ {
	comment_flag=false;
	printf("%s",yytext);
	}
if\(.*\)	{
		if(!comment_flag){
			int len=strlen(yytext);
			char *exp=(char*)malloc(len*sizeof(char));
			memcpy(exp,yytext+3,len-4);
			printf("if((%s)?if_1(%d,\"%s\"):if_0(%d,\"%s\"))",exp,line,exp,line,exp);
		}else{
			printf("%s",yytext);
		}
	}
%%

