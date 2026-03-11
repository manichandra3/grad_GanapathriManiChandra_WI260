import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';

@Component({
  selector: 'app-strength-query',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './strength-query.component.html',
  styleUrl: './strength-query.component.scss'
})
export class StrengthQueryComponent {
  gender = '';
  standard: number | null = null;
  strength: number | null = null;
  loading = false;
  error = '';

  constructor(private studentService: StudentService) {}

  queryStrength(): void {
    if (!this.gender || this.standard === null) return;

    this.loading = true;
    this.error = '';
    this.strength = null;

    this.studentService.getStrength(this.gender, this.standard).subscribe({
      next: (count) => {
        this.strength = count;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to query strength.';
        this.loading = false;
      }
    });
  }
}
