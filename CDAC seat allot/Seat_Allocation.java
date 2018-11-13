import java.io.*;
import java.util.*;

class Student{
	String student_id,option[];
	Integer categoryOneRank,categoryTwoRank,categoryThreeRank;
	String centre_id;		//centre student got alllocated
	Integer choice_no;		//index of choice allocated
	Boolean alloted;		//flag
	
	Student(String id,String c1,String c2,String c3,String choices){
		student_id = id;
		categoryOneRank = Integer.valueOf(c1);
		categoryTwoRank = Integer.valueOf(c2);
		categoryThreeRank = Integer.valueOf(c3);
		
		String rr = choices.replace("|", ",");
		String array[]= rr.split(",");
		choice_no = array.length;
		alloted = false;
		int len = array.length;
		
		option = new String[len];
		
		for(int i=0;i<len;i++){
			option[i]=array[i];
		}
	}
	public String toString(){
		return student_id+","+categoryOneRank+","+categoryTwoRank+","+categoryThreeRank+","+centre_id+","+choice_no+","+alloted;
	}
}

class cat1sort implements Comparator<Student>{
	public int compare(Student a, Student b) 
    { 
        return a.categoryOneRank.compareTo(b.categoryOneRank); 
    }
}

class cat2sort implements Comparator<Student>{
	public int compare(Student a, Student b) 
    { 
        return a.categoryTwoRank.compareTo(b.categoryTwoRank); 
    }
}

class cat3sort implements Comparator<Student>{
	public int compare(Student a, Student b) 
    { 
        return a.categoryThreeRank.compareTo(b.categoryThreeRank);
    }
}

class Course{
	String course_id,course_name;
	Integer capacity,category,seat_vacant,waiting;
		
	Course(String id,String name,String cap,String cat){
		course_id = id;
		course_name = name;
		capacity = Integer.valueOf(cap);
		seat_vacant = capacity;
		category = Integer.valueOf(cat);
		waiting = 0;
	}
	public String toString(){
			return course_id+" "+course_name+" "+capacity+" "+category +" "+seat_vacant+" "+waiting;
	}
}

class Allocote_list{
	String student_id,course_id;
	Integer choice_no;
	Boolean alloted;
	Integer waiting;
	
	Allocote_list(String s_id,String opt,Integer choice,Boolean allot,Integer wait_status){
		student_id = s_id;
		course_id = opt;
		choice_no = choice;
		alloted = allot;
		waiting=wait_status;
	}
	public String toString(){	
			return student_id+" "+course_id+" "+choice_no+"  "+alloted+" "+waiting;  
	}
}

class Seat_Allocation{
	
	static HashMap<String,Course> map = new HashMap<>();
	static ArrayList<Student> list = new ArrayList<Student>();
	static ArrayList<Allocote_list> allocation_list = new ArrayList<Allocote_list>();
		
	public static void main(String args[]){
		String filename = "candidatelist.csv";
		File file = new File(filename);
		Scanner sc = new Scanner(System.in);

		int line = 0;
		try{
			Scanner inputStream = new Scanner(file);
			while(inputStream.hasNext()){
				String data = inputStream.nextLine();
				if(line>0){									//To Avoid first header line
					String array1[]= data.split(",");
					Student s = new Student(array1[0],array1[1],array1[2],array1[3],array1[4]);
					list.add(s);
				}
			line++;
			}
		
/*	------------------------	reading file 2	----------------------*/

			filename = "capacity.csv";
			file = new File(filename);
			line = 0;
			Scanner inputStream1 = new Scanner(file);
			while(inputStream1.hasNext()){
				String data = inputStream1.nextLine();
					String array2[] = data.split(",");
					Course s = new Course(array2[0],array2[1],array2[2],array2[3]);
					map.put(array2[0],s);
			line++;
			}

/*	------------------------	read file 2 complete	--------------------*/
			
			inputStream.close();
			inputStream1.close();

/*	------------------------	Close Input Stream	-----------------------*/
			

/*	------------------------	Sorting List by category 1	-----------------*/

			cat1sort s1 = new cat1sort();
			Collections.sort(list,s1);

/*	------------------------	Allocating According Category 1		----------*/

			for(int i=0;i<list.size();i++){
				Student temp = list.get(i);
				for(int j=0;j<temp.option.length && temp.categoryOneRank!=99999;j++){
					Course temp2 = map.get(temp.option[j]);
					if(temp2.category==1){
						if(temp2.seat_vacant>0){	
							temp.centre_id = temp.option[j];
							temp.choice_no = j;
							temp.alloted = true;
							temp2.seat_vacant = temp2.seat_vacant - 1;
							
							Allocote_list a = new Allocote_list(temp.student_id,temp.option[j],j,true,-1);
							allocation_list.add(a);
						break;
						}
						else if(temp2.seat_vacant<=0){
							temp2.waiting++;
							Allocote_list b = new Allocote_list(temp.student_id,temp.option[j],j,false,temp2.waiting);
							allocation_list.add(b);
						}
					}
				}
			}
		
/*	------------------------	End of 1st Allocation	-------------------*/

/*	------------------------	Sorting According to Category 2		---------------------*/

			cat2sort s2 = new cat2sort();
			Collections.sort(list,s2);
			
/*	------------------------	Allocote_list according to Category 2	----------------------*/
			for(int i=0;i<list.size();i++){
				Student temp = list.get(i);
				for(int j=0;j<Math.min(temp.option.length,temp.choice_no) && temp.categoryTwoRank!=99999;j++){
					Course temp2 = map.get(temp.option[j]);
					if(temp2.category==2){
						if(temp2.seat_vacant>0){
							temp.centre_id = temp.option[j];
							temp.choice_no = j;
							temp.alloted = true;
							temp2.seat_vacant--;
							
							Allocote_list a = new Allocote_list(temp.student_id,temp.option[j],j,true,-1);
							allocation_list.add(a);
							remove_gt_waiting_choice(temp.student_id,j);
						break;
						}
						else if(temp2.seat_vacant<=0){
							temp2.waiting++;
							Allocote_list b = new Allocote_list(temp.student_id,temp.option[j],j,false,temp2.waiting);
							allocation_list.add(b);
						}
					}
				}
			}

/*	--------------------    End Allocation by Category 2	------------------------*/

/*	--------------------	sort According to category 3	-------------------------*/
			
			cat3sort s3 = new cat3sort();
			Collections.sort(list,s3);
			
/*	-------------------		Allocation by Category 3 Start	-------------------------*/

			for(int i=0;i<list.size();i++){
				Student temp = list.get(i);
				for(int j=0;j<Math.min(temp.option.length,temp.choice_no) && temp.categoryThreeRank!=99999;j++){
					Course temp2 = map.get(temp.option[j]);
					if(temp2.category==3){
						if(temp2.seat_vacant>0){
							temp.centre_id = temp.option[j];
							temp.choice_no = j;
							temp.alloted = true;
							temp2.seat_vacant = temp2.seat_vacant - 1;
							
							Allocote_list a = new Allocote_list(temp.student_id,temp.option[j],j,true,0);
							allocation_list.add(a);
							remove_gt_waiting_choice(temp.student_id,j);
						break;
						}
						else{
							temp2.waiting++;
							Allocote_list b = new Allocote_list(temp.student_id,temp.option[j],j,false,temp2.waiting);
							allocation_list.add(b);
						}
					}
				}
			}
			
/*	--------------------	final third Allocation Ended	---------------------*/


// Uncomment this to get output
// eg: you will will get list of student selected for CDAC Kharghar and like wise all centre AS centre wise file
/* 
			for(Map.Entry<String,Course> entry : map.entrySet()){
				String output_file_name=entry.getKey()+".txt";
				PrintStream o = new PrintStream(new File(output_file_name));
				System.setOut(o);
				for(int i=0;i<list.size();i++){
					if(list.get(i).centre_id!=null){
						if(list.get(i).centre_id.equals(entry.getKey())){
							System.out.println(list.get(i));
						}
					}
				}
			} 
*/

/*	--------------------	Menu Driven		---------------------------*/	
			int ch=0;
			while(ch!=5){
				System.out.println("1.List of student got allotment\n2.List of student not got allotment\n3.List of colleges\n4.Each College Wise List\n5.exit");
				ch = sc.nextInt();
				switch(ch){
					case 1:
						alloted_list_student();
					break;
					case 2:
						not_alloted_list_student();
					break;
					case 3:
						college_list();
					break;
					case 4:
						individual_college_list();
					break;
					case 5:
						ch=5	;
					break;
					default:
						System.out.println("Enter proper Choice");
					break;
				}
			}			
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
	}
	
	static void remove_gt_waiting_choice(String s_id,Integer ch_no){
		for(int i=0;i<allocation_list.size();i++){
			Allocote_list temp = allocation_list.get(i);
			
			if(temp.student_id.equals(s_id) && temp.choice_no>ch_no){
				Course temp2 = map.get(temp.course_id);
				allocation_list.remove(i);
				i--;
				if(temp.alloted==true){
					temp2.seat_vacant++;
					allot_wait_student(temp.course_id);
				}
				else{
					temp2.waiting--;
				}
			}
		}
	}
	static void allot_wait_student(String centre){
		Course temp2 = map.get(centre);
		for(int i=0;i<allocation_list.size();i++){
			Allocote_list temp = allocation_list.get(i);
			if(temp.course_id.equals(centre) && temp.alloted==false){
				temp.alloted=true;
				temp.waiting=-1;
				temp2.seat_vacant--;
				for(int j=0;j<list.size();j++){
					Student added_student = list.get(j);
					if(added_student.student_id.equals(temp.student_id)){
						
						added_student.centre_id = centre;
						added_student.choice_no = temp.choice_no;
						added_student.alloted = true;
					temp2.waiting--;
					}
				}
				remove_gt_waiting_choice(temp.student_id,temp.choice_no);
			break;
			}
		}
	}
	static void alloted_list_student()throws FileNotFoundException{
		int select_count=0;
		for(int i=0;i<list.size();i++){
			if(list.get(i).alloted==true){
				System.out.println(list.get(i));
				select_count++;
			}
		}
	System.out.println(" select_count  =  "+select_count);
	}
	static void not_alloted_list_student()throws FileNotFoundException{
		int not_select_count=0;
		for(int i=0;i<list.size();i++){
			if(list.get(i).alloted==false){
				System.out.println(list.get(i));
				not_select_count++;
			}
		}
	System.out.println(" not select count  =  "+not_select_count);
	}
	static void college_list()throws FileNotFoundException{
		for(Map.Entry<String,Course> entry : map.entrySet()){
			System.out.println("Value = " + entry.getValue());
		}
	}
	static void individual_college_list(){
		int select_count=-1;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter College id");
		String cname = sc.next();
		for(int i=0;i<list.size();i++){
			if(list.get(i).centre_id!=null){
				if(list.get(i).centre_id.equals(cname)){
					System.out.println(list.get(i));
					select_count++;
				}
			}
		}
		System.out.println(" select_count  =  "+(select_count+1));
		if(select_count==-1){
			System.out.println(" Please Enter Proper choice \n");
		}
	}
}