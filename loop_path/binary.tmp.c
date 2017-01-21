#include<stdio.h>
int binarySearch(int array[],int size,int target){
	int low=0,high=size-1;
	while((cond(4,1,low<=high,"low<=high"))){
		int mid=(low+high)/2;
		if((cond(6,1,array[mid]==target,"array[mid]==target"))){
				return mid;
		}else if((cond(8,1,array[mid]>target,"array[mid]>target"))){
				high=mid-1;
		}else{
				low=mid+1;
		}
	}
	return -1;
}
int main(int argc,char *argv[]){
	int size=argc-2;
	int a[size];
	printf("size= %d\n",size);
	int i=0;
	for(i=0;(cond(21,1,i<size,"i<size"));i++){
		a[i]=atoi(argv[i+1]);
		printf("a[%d]= %d\n",i,a[i]);
	}
	int target=atoi(argv[i+1]);
	printf("%d\n",binarySearch(a,size,target));

    return 0;
}


//4
