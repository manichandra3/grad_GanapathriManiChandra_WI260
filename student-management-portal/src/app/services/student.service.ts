import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Student } from '../models/student.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private baseUrl = 'http://localhost:8181/students';

  constructor(private http: HttpClient) {}

  // GET /students
  getAllStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.baseUrl);
  }

  // GET /students/{regNo}
  getStudentByRegNo(regNo: number): Observable<Student> {
    return this.http.get<Student>(`${this.baseUrl}/${regNo}`);
  }

  // POST /students
  addStudent(student: Student): Observable<string> {
    return this.http.post(this.baseUrl, student, { responseType: 'text' });
  }

  // PUT /students/{regNo}
  updateStudent(regNo: number, student: Student): Observable<string> {
    return this.http.put(`${this.baseUrl}/${regNo}`, student, { responseType: 'text' });
  }

  // PATCH /students/{regNo}
  patchStudent(regNo: number, fields: Partial<Student>): Observable<string> {
    return this.http.patch(`${this.baseUrl}/${regNo}`, fields, { responseType: 'text' });
  }

  // DELETE /students/{regNo}
  deleteStudent(regNo: number): Observable<string> {
    return this.http.delete(`${this.baseUrl}/${regNo}`, { responseType: 'text' });
  }

  // GET /students/school?name=
  getStudentsBySchool(name: string): Observable<Student[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Student[]>(`${this.baseUrl}/school`, { params });
  }

  // GET /students/school/count?name=
  getSchoolCount(name: string): Observable<number> {
    const params = new HttpParams().set('name', name);
    return this.http.get<number>(`${this.baseUrl}/school/count`, { params });
  }

  // GET /students/school/standard/count?class=
  getStandardCount(standard: number): Observable<number> {
    const params = new HttpParams().set('class', standard.toString());
    return this.http.get<number>(`${this.baseUrl}/school/standard/count`, { params });
  }

  // GET /students/result?pass=true/false
  getResultStudents(pass: boolean): Observable<Student[]> {
    const params = new HttpParams().set('pass', pass.toString());
    return this.http.get<Student[]>(`${this.baseUrl}/result`, { params });
  }

  // GET /students/strength?gender=&standard=
  getStrength(gender: string, standard: number): Observable<number> {
    const params = new HttpParams()
      .set('gender', gender)
      .set('standard', standard.toString());
    return this.http.get<number>(`${this.baseUrl}/strength`, { params });
  }
}
