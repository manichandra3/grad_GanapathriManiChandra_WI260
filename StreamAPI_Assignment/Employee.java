/*
Employee
----------
name
age
gender
salary
designation
department
 
 
* Find the highest salary paid employee
* Find how many male & female employees working in company (numbers)
* Total expense for the company department wise
* Who is the top 5 senior employees in the company
* Find only the names who all are managers
* Hike the salary by 20% for everyone except manager
* Find the total number of employees
*/

enum Designation
{
  CLERK(1),
  INTERN(2),
  SOFTWARE_ENGINEER(3),
  LEAD_ENGINEER(4),
  MANAGER(5),
  CEO(6);

  private final int level;

  Designation(int level) {
    this.level = level;
  }

  public boolean isManagerRole(){
    return this.level == 5;
  }

  public boolean isSeniorRole(){
    return this.level >= 4;
  }
}

enum Department {
  CLERICAL(1),
  ENGINEERING(2),
  LEADERSHIP(3);
  
  private final int id;
  
  Department(int id) {
    this.id = id;
  }
} 

public class Employee {
  private String name;
  private int age;
  private String gender;
  private double salary;
  private Designation designation;
  private Department department;
  
  public Employee(String name, int age, String gender, double salary, Designation designation, Department department)
{
  this.name = name;
  this.age = age;
  this.gender = gender;
  this.salary = salary;
  this.designation = designation;
  this.department = department;
}

  public String getName(){return name;}
  public int getAge(){return age;}
  public String getGender(){return gender;}
  public double getSalary(){return salary;}
  public Designation getDesignation(){return designation;}
  public Department getDepartment(){return department;}

  public void setSalary(double salary) {
      this.salary = salary;
  }

  @Override
  public String toString() {
      return String.format("Employee{name='%s', age=%d, salary=%.2f, designation=%s}", name, age, salary, designation);
  }
}




