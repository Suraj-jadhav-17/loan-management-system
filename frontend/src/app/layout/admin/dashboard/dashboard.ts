import { Component } from '@angular/core';
import { Navbar } from '../../../shared/components/admin/navbar/navbar';

@Component({
  imports: [Navbar],
  selector: 'app-dashboard',
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html',
})
export class Dashboard {}
