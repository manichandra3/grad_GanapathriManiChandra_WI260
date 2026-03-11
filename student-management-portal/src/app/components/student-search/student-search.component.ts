import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './student-search.component.html',
  styleUrl: './student-search.component.scss'
})
export class StudentSearchComponent {
  searchRegNo: number | null = null;
  student: Student | null = null;
  loading = false;
  error = '';
  successMessage = '';

  // Patch fields
  showPatchForm = false;
  patchFields: { [key: string]: any } = {};
  patchableFields = ['rollNumber', 'name', 'standard', 'school', 'gender', 'percentage'];

  constructor(private studentService: StudentService) {}

  searchStudent(): void {
    if (!this.searchRegNo) return;

    this.loading = true;
    this.error = '';
    this.successMessage = '';
    this.student = null;
    this.showPatchForm = false;

    this.studentService.getStudentByRegNo(this.searchRegNo).subscribe({
      next: (data) => {
        this.student = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 404
          ? `No student found with registration number ${this.searchRegNo}.`
          : 'Failed to search for student.';
        this.loading = false;
      }
    });
  }

  togglePatchForm(): void {
    this.showPatchForm = !this.showPatchForm;
    if (this.showPatchForm && this.student) {
      this.patchFields = {};
    }
  }

  submitPatch(): void {
    if (!this.student) return;

    const fieldsToUpdate: { [key: string]: any } = {};
    for (const key of Object.keys(this.patchFields)) {
      if (this.patchFields[key] !== undefined && this.patchFields[key] !== '') {
        let value = this.patchFields[key];
        if (['rollNumber', 'standard', 'percentage'].includes(key)) {
          value = Number(value);
        }
        fieldsToUpdate[key] = value;
      }
    }

    if (Object.keys(fieldsToUpdate).length === 0) {
      this.error = 'Please fill in at least one field to update.';
      return;
    }

    this.error = '';
    this.successMessage = '';

    this.studentService.patchStudent(this.student.registrationNumber, fieldsToUpdate).subscribe({
      next: (message) => {
        this.successMessage = message;
        this.showPatchForm = false;
        // Refresh student data
        this.searchStudent();
      },
      error: (err) => {
        this.error = err.error || 'Failed to update student.';
      }
    });
  }

  deleteStudent(): void {
    if (!this.student) return;
    if (!confirm(`Are you sure you want to delete student "${this.student.name}"?`)) return;

    this.studentService.deleteStudent(this.student.registrationNumber).subscribe({
      next: (message) => {
        this.successMessage = message;
        this.student = null;
      },
      error: (err) => {
        this.error = err.error || 'Failed to delete student.';
      }
    });
  }
}
