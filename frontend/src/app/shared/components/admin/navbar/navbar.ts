import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  imports: [RouterLink, RouterLinkActive],
  selector: 'app-navbar',
  styleUrl: './navbar.scss',
  templateUrl: './navbar.html',
})
export class Navbar {
  openMenu: string | null = '';

  toggleMenu(menu: string): void {
    this.openMenu = this.openMenu === menu ? null : menu;
  }
}
