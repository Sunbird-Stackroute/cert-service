import { Injectable, inject, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';

export const lightTheme = {
  'main-color': '#fff',
  'background-color': '#e5e5e5',
  'text-color': '#2d2d2d'
};

export const darkTheme = {
  'main-color': '#455363',
  'background-color': '#1f2935',
  'text-color': '#fff'
};

@Injectable({
  providedIn: 'root'
})

export class ThemeService {

  toggleDark() {
    this.setTheme(darkTheme);
  }

  toggleLight() {
    this.setTheme(lightTheme);
  }

  private setTheme(theme: {}) {
    Object.keys(theme).forEach(value =>
      document.documentElement.style.setProperty(`--${value}`, theme[value])
    )
  }
}
