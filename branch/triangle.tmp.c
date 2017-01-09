#include<stdio.h>
#include<stdbool.h>
bool judge(int a,int b,int c){
	if((a+b>c&&b+c>a&&a+c>b)?if_1(4,"a+b>c&&b+c>a&&a+c>b"):if_0(4,"a+b>c&&b+c>a&&a+c>b")){
		if((a==b)?if_1(5,"a==b"):if_0(5,"a==b")){
			if((b==c)?if_1(6,"b==c"):if_0(6,"b==c")){
				printf("Equilateral triangle!\n");
			}else{
				printf("Isosceles triangle!\n");
			}
		}else{
			if((b==c)?if_1(12,"b==c"):if_0(12,"b==c")){
				printf("Isosceles triangle!\n");
			}else{
				if((c==a)?if_1(15,"c==a"):if_0(15,"c==a")){
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
	if((argc<4)?if_1(28,"argc<4"):if_0(28,"argc<4")){
		printf("Need three args!\n");
		return 0;
	}
	int a=atoi(argv[1]);
	int b=atoi(argv[2]);
	int c=atoi(argv[3]);
	printf("%d",judge(a,b,c));
	return 0;
}
//6
