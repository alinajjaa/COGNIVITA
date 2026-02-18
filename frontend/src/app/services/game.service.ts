// src/app/services/game.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface GameState {
  activityId: number;
  currentQuestion: number;
  score: number;
  timeLeft: number;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'ABANDONED';
  answers: any[];
}

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private gameState = new BehaviorSubject<GameState | null>(null);
  private timer: any;

  constructor() { }

  initGame(activity: any): void {
    const state: GameState = {
      activityId: activity.id,
      currentQuestion: 0,
      score: 0,
      timeLeft: activity.timeLimit || 60,
      status: 'IN_PROGRESS',
      answers: []
    };
    this.gameState.next(state);
    this.startTimer();
  }

  private startTimer(): void {
    this.timer = setInterval(() => {
      const current = this.gameState.value;
      if (current && current.status === 'IN_PROGRESS') {
        if (current.timeLeft <= 1) {
          this.completeGame();
        } else {
          this.gameState.next({
            ...current,
            timeLeft: current.timeLeft - 1
          });
        }
      }
    }, 1000);
  }

  submitAnswer(answer: any, isCorrect: boolean): void {
    const current = this.gameState.value;
    if (current && current.status === 'IN_PROGRESS') {
      const newScore = isCorrect ? current.score + 10 : current.score;
      this.gameState.next({
        ...current,
        score: newScore,
        answers: [...current.answers, answer]
      });
    }
  }

  nextQuestion(): void {
    const current = this.gameState.value;
    if (current) {
      this.gameState.next({
        ...current,
        currentQuestion: current.currentQuestion + 1
      });
    }
  }

  completeGame(): void {
    if (this.timer) clearInterval(this.timer);
    const current = this.gameState.value;
    if (current) {
      this.gameState.next({
        ...current,
        status: 'COMPLETED'
      });
    }
  }

  abandonGame(): void {
    if (this.timer) clearInterval(this.timer);
    const current = this.gameState.value;
    if (current) {
      this.gameState.next({
        ...current,
        status: 'ABANDONED'
      });
    }
  }

  getGameState(): Observable<GameState | null> {
    return this.gameState.asObservable();
  }

  resetGame(): void {
    if (this.timer) clearInterval(this.timer);
    this.gameState.next(null);
  }
}
