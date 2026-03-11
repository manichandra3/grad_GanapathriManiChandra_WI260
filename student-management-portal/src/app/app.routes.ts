import { Routes } from '@angular/router';
import { StudentListComponent } from './components/student-list/student-list.component';
import { StudentDetailComponent } from './components/student-detail/student-detail.component';
import { StudentFormComponent } from './components/student-form/student-form.component';
import { StudentSearchComponent } from './components/student-search/student-search.component';
import { SchoolFilterComponent } from './components/school-filter/school-filter.component';
import { ResultFilterComponent } from './components/result-filter/result-filter.component';
import { StrengthQueryComponent } from './components/strength-query/strength-query.component';

export const routes: Routes = [
  { path: '', redirectTo: 'students', pathMatch: 'full' },
  { path: 'students', component: StudentListComponent },
  { path: 'students/add', component: StudentFormComponent },
  { path: 'students/search', component: StudentSearchComponent },
  { path: 'students/edit/:regNo', component: StudentFormComponent },
  { path: 'students/:regNo', component: StudentDetailComponent },
  { path: 'school', component: SchoolFilterComponent },
  { path: 'results', component: ResultFilterComponent },
  { path: 'strength', component: StrengthQueryComponent },
];
