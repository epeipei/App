#include<stdio.h>
#include<stdbool.h>
bool judge(int a,int b,int c){
	if((cond(4,1,a+b>c,"a+b>c"))&&(cond(4,2,b+c>a,"b+c>a"))&&(cond(4,3,a+c>b,"a+c>b"))){
		if((cond(5,1,a==b,"a==b"))){
			if((cond(6,1,b==c,"b==c"))){
				printf("等边三角形\n");
			}else{
				printf("等腰三角形\n");
			}
		}else{
			if((cond(12,1,b==c,"b==c"))){
				printf("等腰三角形\n");
			}else{
				if((cond(15,1,c==a,"c==a"))){
					printf("等腰三角形\n");
				}else{
					printf("不等边三角形\n");
				}
			}
		}
	}else{
		printf("不构成三角形\n");
	}
	return 0;
}
int main(int argc,char *argv[]){
	if((cond(28,1,argc<4,"argc<4"))){
		printf("需要三个参数\n");
		return 0;
	}
	int a=atoi(argv[1]);
	int b=atoi(argv[2]);
	int c=atoi(argv[3]);
	printf("%d",judge(a,b,c));
	return 0;
}
