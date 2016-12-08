#include<stdio.h>
#include<stdbool.h>
bool judge(int a,int b,int c){
	if((a+b>c&&b+c>a&&a+c>b)?if_1(4,"a+b>c&&b+c>a&&a+c>b"):if_0(4,"a+b>c&&b+c>a&&a+c>b")){
		if((a==b)?if_1(5,"a==b"):if_0(5,"a==b")){
			if((b==c)?if_1(6,"b==c"):if_0(6,"b==c")){
				printf("等边三角形\n");
			}else{
				printf("等腰三角形\n");
			}
		}else{
			if((b==c)?if_1(12,"b==c"):if_0(12,"b==c")){
				printf("等腰三角形\n");
			}else{
				if((c==a)?if_1(15,"c==a"):if_0(15,"c==a")){
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
	if((argc<4)?if_1(28,"argc<4"):if_0(28,"argc<4")){
		printf("需要三个参数\n");
		return 0;
	}
	int a=atoi(argv[1]);
	int b=atoi(argv[2]);
	int c=atoi(argv[3]);
	printf("%d",judge(a,b,c));
	return 0;
}
