import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StudentService } from '../../services/student.service';
import { Student } from '../../models/student.model';

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './student-form.component.html',
  styleUrl: './student-form.component.scss'
})
export class StudentFormComponent implements OnInit {
  studentForm!: FormGroup;
  isEditMode = false;
  regNo: number | null = null;
  loading = false;
  submitLoading = false;
  error = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private studentService: StudentService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();

    const regNoParam = this.route.snapshot.paramMap.get('regNo');
    if (regNoParam) {
      this.isEditMode = true;
      this.regNo = Number(regNoParam);
      this.loadStudent(this.regNo);
    }
  }

  private initForm(): void {
    this.studentForm = this.fb.group({
      registrationNumber: [null, [Validators.required, Validators.min(1)]],
      rollNumber: [null, [Validators.required, Validators.min(1)]],
      name: ['', [Validators.required, Validators.minLength(2)]],
      standard: [null, [Validators.required, Validators.min(1), Validators.max(12)]],
      school: ['', [Validators.required]],
      gender: ['', [Validators.required]],
      percentage: [null, [Validators.required, Validators.min(0), Validators.max(100)]]
    });
  }

  private loadStudent(regNo: number): void {
    this.loading = true;
    this.studentService.getStudentByRegNo(regNo).subscribe({
      next: (student) => {
        this.studentForm.patchValue(student);
        this.studentForm.get('registrationNumber')?.disable();
        this.loading = false;
      },
      error: (err) => {
        this.error = err.status === 404
          ? `Student with registration number ${regNo} not found.`
          : 'Failed to load student data.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.studentForm.invalid) {
      this.studentForm.markAllAsTouched();
      return;
    }

    this.submitLoading = true;
    this.error = '';
    this.successMessage = '';

    const formValue = this.studentForm.getRawValue() as Student;

    if (this.isEditMode && this.regNo !== null) {
      this.studentService.updateStudent(this.regNo, formValue).subscribe({
        next: (message) => {
          this.successMessage = message;
          this.submitLoading = false;
          setTimeout(() => this.router.navigate(['/students']), 1500);
        },
        error: (err) => {
          this.error = err.error || 'Failed to update student.';
          this.submitLoading = false;
        }
      });
    } else {
      this.studentService.addStudent(formValue).subscribe({
        next: (message) => {
          this.successMessage = message;
          this.submitLoading = false;
          setTimeout(() => this.router.navigate(['/students']), 1500);
        },
        error: (err) => {
          this.error = err.error || 'Failed to add student.';
          this.submitLoading = false;
        }
      });
    }
  }

  isFieldInvalid(field: string): boolean {
    const control = this.studentForm.get(field);
    return !!(control && control.invalid && control.touched);
  }

  getFieldError(field: string): string {
    const control = this.studentForm.get(field);
    if (!control || !control.errors) return '';

    if (control.errors['required']) return `${field} is required.`;
    if (control.errors['min']) return `Minimum value is ${control.errors['min'].min}.`;
    if (control.errors['max']) return `Maximum value is ${control.errors['max'].max}.`;
    if (control.errors['minlength']) return `Minimum length is ${control.errors['minlength'].requiredLength}.`;

    return 'Invalid value.';
  }
}
