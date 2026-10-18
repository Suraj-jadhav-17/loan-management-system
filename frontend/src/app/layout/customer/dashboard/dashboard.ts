import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Navbar } from '../../../shared/components/customer/navbar/navbar';
import { Topbar } from '../../../shared/components/customer/topbar/topbar';

@Component({
  imports: [Navbar, Topbar, RouterOutlet],
  selector: 'app-customer-dashboard',
  styleUrl: './dashboard.scss',
  templateUrl: './dashboard.html',
})
export class Dashboard {}
