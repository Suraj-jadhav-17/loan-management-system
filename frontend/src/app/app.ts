import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {  Dashboard as CustomerLayout} from './layout/customer/dashboard/dashboard';

@Component({
  imports: [RouterOutlet, CustomerLayout],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  protected readonly title = signal('loan-frontend');
}
