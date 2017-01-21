%{
#include<stdio.h>
#include<string.h>
#include<stdbool.h>
int line=1;
int total=0;
int cs=0;//表示每一行的条件的个数，计数
bool comment_flag=false;//for /* comment
void func(char *a,char *b){
	printf("if(%s>%s){printf(\"1\");}\n",a,b);
	printf("if(%s<%s){printf(\"-1\");}\n",a,b);
	printf("if(%s==%s){printf(\"0\");}\n",a,b);
}
int cond(int line,int cs,int l,int r,char *condition){
	if(l>r){
		printf("\nmycov %d %d %d %s\n",line,cs,1,condition);
		return 1;
	}
	if(l==r){	
		printf("\nmycov %d %d %d %s\n",line,cs,0,condition);
		return 0;
	}
	if(l<r){
		printf("\nmycov %d %d %d %s\n",line,cs,-1,condition);
		return -1;
	}
}

%}
SPACE	[ \t\n]*
DIGIT	[0-9]
ID		[a-zA-Z_][a-zA-Z0-9_]*
DOUBLE	{DIGIT}+"."{DIGIT}*
OP		"+"|"-"|"*"|"/"
ROP		>|<|>=|<=|==
INT		{DIGIT}+
DOT		"."
META	{ID}|{INT}|{DOUBLE}
EXP		{META}({OP}{META})*
LOP		"&&"|"||"
COND	{RCC}({LOP}{RCC})*
RCC		{EXP}{ROP}{EXP}
%%
\n {
	line++;
	cs=0;
	printf("\n");
	}
#.* {
	printf("%s",yytext);
	}
\".*\"	{
	printf("%s",yytext);
	}
\/\/.* {//for comment // line
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
{RCC}	{
		if(!comment_flag){//not the comment
			int len=strlen(yytext)+1;
			char *rcc=(char*)malloc(len*sizeof(char));
   //		memcpy(exp,yytext+3,len-4);
			strcpy(rcc,yytext);
			char *op="<=|>=|<|>|==";
			char *l=strtok(rcc,op);
			char *r=strtok(NULL,op);
			cs++;
			total++;
			printf("(cond(%d,%d,%s,%s,\"%s\"),%s)",line,cs,l,r,yytext,yytext);
		}else{
			printf("%s",yytext);
		}
	}
%%
int main(){
	yylex();
	printf("//%d\n",total);
	return 0;
}
