import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-result-filter',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './result-filter.component.html',
  styleUrl: './result-filter.component.scss'
})
export class ResultFilterComponent {
  students: Student[] = [];
  loading = false;
  error = '';
  currentFilter: 'pass' | 'fail' | null = null;

  constructor(private studentService: StudentService) {}

  filterResults(pass: boolean): void {
    this.loading = true;
    this.error = '';
    this.currentFilter = pass ? 'pass' : 'fail';

    this.studentService.getResultStudents(pass).subscribe({
      next: (data) => {
        this.students = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to fetch results.';
        this.loading = false;
      }
    });
  }
}
