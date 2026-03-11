import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './student-detail.component.html',
  styleUrl: './student-detail.component.scss'
})
export class StudentDetailComponent implements OnInit {
  student: Student | null = null;
  loading = true;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private studentService: StudentService
  ) {}

  ngOnInit(): void {
    const regNo = Number(this.route.snapshot.paramMap.get('regNo'));
    if (isNaN(regNo)) {
      this.error = 'Invalid registration number.';
      this.loading = false;
      return;
    }

    this.studentService.getStudentByRegNo(regNo).subscribe({
      next: (data) => {
        this.student = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 404
          ? `Student with registration number ${regNo} not found.`
          : 'Failed to load student details.';
        this.loading = false;
      }
    });
  }

  getPassStatus(percentage: number): string {
    return percentage >= 40 ? 'Pass' : 'Fail';
  }

  deleteStudent(): void {
    if (!this.student) return;
    if (!confirm(`Are you sure you want to delete student "${this.student.name}"?`)) return;

    this.studentService.deleteStudent(this.student.registrationNumber).subscribe({
      next: () => {
        this.router.navigate(['/students']);
      },
      error: (err) => {
        this.error = 'Failed to delete student.';
      }
    });
  }
}
