import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-school-filter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './school-filter.component.html',
  styleUrl: './school-filter.component.scss'
})
export class SchoolFilterComponent {
  // School name search
  schoolName = '';
  students: Student[] = [];
  schoolCount: number | null = null;
  loading = false;
  error = '';

  // Standard count search
  standardInput: number | null = null;
  standardCount: number | null = null;
  standardLoading = false;
  standardError = '';

  constructor(private studentService: StudentService) {}

  searchBySchool(): void {
    if (!this.schoolName.trim()) return;

    this.loading = true;
    this.error = '';
    this.students = [];
    this.schoolCount = null;

    this.studentService.getStudentsBySchool(this.schoolName.trim()).subscribe({
      next: (data) => {
        this.students = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to fetch students by school.';
        this.loading = false;
      }
    });
  }

  getSchoolCount(): void {
    if (!this.schoolName.trim()) return;

    this.studentService.getSchoolCount(this.schoolName.trim()).subscribe({
      next: (count) => {
        this.schoolCount = count;
      },
      error: (err) => {
        this.error = 'Failed to get school count.';
      }
    });
  }

  searchAndCount(): void {
    this.searchBySchool();
    this.getSchoolCount();
  }

  searchByStandard(): void {
    if (this.standardInput === null) return;

    this.standardLoading = true;
    this.standardError = '';
    this.standardCount = null;

    this.studentService.getStandardCount(this.standardInput).subscribe({
      next: (count) => {
        this.standardCount = count;
        this.standardLoading = false;
      },
      error: (err) => {
        this.standardError = 'Failed to get standard count.';
        this.standardLoading = false;
      }
    });
  }
}
