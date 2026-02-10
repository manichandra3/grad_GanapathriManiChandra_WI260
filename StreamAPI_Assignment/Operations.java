import java.util.*;
import java.util.stream.*;

public class Operations {
    public static void main(String[] args) {
        
        List<Employee> employees = Arrays.asList(
            new Employee("Alice", 25, "Female", 50000, Designation.SOFTWARE_ENGINEER, Department.ENGINEERING),
            new Employee("Bob", 45, "Male", 150000, Designation.MANAGER, Department.LEADERSHIP),
            new Employee("Charlie", 30, "Male", 80000, Designation.LEAD_ENGINEER, Department.ENGINEERING),
            new Employee("Diana", 22, "Female", 30000, Designation.INTERN, Department.ENGINEERING),
            new Employee("Edward", 55, "Male", 300000, Designation.CEO, Department.LEADERSHIP),
            new Employee("Fiona", 38, "Female", 120000, Designation.MANAGER, Department.LEADERSHIP),
            new Employee("George", 21, "Male", 25000, Designation.CLERK, Department.CLERICAL)
        ); 

        // 1. Highest salary paid employee
        employees.stream()
                 .max(Comparator.comparingDouble(Employee::getSalary))
                 .ifPresent(e -> System.out.println("Highest Salary: " + e.getName()));

        // 2. Male and female counts
        Map<String, Long> genderCount = employees.stream()
                 .collect(Collectors.groupingBy(Employee::getGender, Collectors.counting()));
        System.out.println("Gender Count: " + genderCount);

        // 3. Total expense Dept wise
        Map<Department, Double> totalExpenseByDept = employees.stream()
                 .collect(Collectors.groupingBy(
                      Employee::getDepartment,
                      Collectors.summingDouble(Employee::getSalary)
                 ));
        System.out.println("Department Expenses: " + totalExpenseByDept);

        // 4. Top 5 senior employees (By Age)
        List<Employee> top5Seniors = employees.stream()
                  .sorted(Comparator.comparingInt(Employee::getAge).reversed())
                  .limit(5)
                  .collect(Collectors.toList());
        System.out.println("Top 5 Seniors: " + top5Seniors);

        // 5. Manager names
        List<String> managerNames = employees.stream()
                  .filter(e -> e.getDesignation().isManagerRole())
                  .map(Employee::getName)
                  .collect(Collectors.toList());
        System.out.println("Managers: " + managerNames);

        // 6. 20% Hike for non-managers
        employees.stream()
                  .filter(e -> !e.getDesignation().isManagerRole())
                  .forEach(e -> e.setSalary(e.getSalary() * 1.20));
        System.out.println("Salaries updated (excluding managers).");

        // 7. Total number of employees
        long totalEmployees = employees.size();
        System.out.println("Total Employees: " + totalEmployees);
    }
}
