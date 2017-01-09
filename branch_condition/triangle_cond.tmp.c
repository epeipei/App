#include<stdio.h>
#include<stdbool.h>
bool judge(int a,int b,int c){
	if((cond(4,1,a+b>c,"a+b>c"))&&(cond(4,2,b+c>a,"b+c>a"))&&(cond(4,3,a+c>b,"a+c>b"))){
		if((cond(5,1,a==b,"a==b"))){
			if((cond(6,1,b==c,"b==c"))){
				printf("Equilateral triangle!\n");
			}else{
				printf("Isosceles triangle!\n");
			}
		}else{
			if((cond(12,1,b==c,"b==c"))){
				printf("Isosceles triangle!\n");
			}else{
				if((cond(15,1,c==a,"c==a"))){
					printf("Isosceles triangle!\n");
				}else{
					printf("Not Equilateral triangle!\n");
				}
			}
		}
	}else{
		printf("Does not constitute a triangle!\n");
	}
	return 0;
}
int main(int argc,char *argv[]){
	if((cond(28,1,argc<4,"argc<4"))){
		printf("Need three args!\n");
		return 0;
	}
	int a=atoi(argv[1]);
	int b=atoi(argv[2]);
	int c=atoi(argv[3]);
	printf("%d",judge(a,b,c));
	return 0;
}
//8
