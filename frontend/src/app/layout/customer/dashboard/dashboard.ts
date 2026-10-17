import { Component } from '@angular/core';
import { Navbar } from '../../../shared/components/customer/navbar/navbar';
import { Topbar } from '../../../shared/components/customer/topbar/topbar';
import { RouterOutlet } from '@angular/router';

@Component({
  imports: [Navbar,Topbar,RouterOutlet],
  selector: 'app-customer-dashboard',
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html',
})
export class Dashboard {}
