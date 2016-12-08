%{
#include<stdio.h>
#include<string.h>
#include<stdbool.h>
int line=1;
int cs=0;//表示每一行的条件的个数，计数
bool comment_flag=false;//for /* comment
void func(char *a,char *b){
	printf("if(%s>%s){printf(\"1\");}\n",a,b);
	printf("if(%s<%s){printf(\"-1\");}\n",a,b);
	printf("if(%s==%s){printf(\"0\");}\n",a,b);
}
int cond_3(int line,int cs,int l,int r,char *condition){
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

bool cond_1(int line,int cs,char *condition){
		printf("\nmycov %d %d %d %s\n",line,cs,1,condition);
		return true;
}

bool cond_0(int line,int cs,char *condition){
		printf("\nmycov %d %d %d %s\n",line,cs,0,condition);
		return false;
}

bool cond(int line,int cs,bool b,char *condition){
		int result=-1;
		if(b){
			result=1;
		}else{
			result=0;
		}
		printf("\nmycov %d %d %d %s\n",line,cs,result,condition);
		return b;
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
		//	int len=strlen(yytext)+1;
		//	char *rcc=(char*)malloc(len*sizeof(char));
   //		memcpy(exp,yytext+3,len-4);
		//	strcpy(rcc,yytext);
		//	char *op="<=|>=|<|>|==";
		//	char *l=strtok(rcc,op);
		//	char *r=strtok(NULL,op);
			cs++;
		//	printf("((%s)?cond_1(%d,%d,\"%s\"):cond_0(%d,%d,\"%s\"))",
	//		printf("(cond(%d,%d,%s,%s,\"%s\"),%s)",line,cs,l,r,yytext,yytext);
			printf("(cond(%d,%d,%s,\"%s\"))",line,cs,yytext,yytext);
		}else{
			printf("%s",yytext);
		}
	}
%%

