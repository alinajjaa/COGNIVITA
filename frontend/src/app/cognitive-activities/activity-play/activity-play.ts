// src/app/cognitive-activities/activity-play/activity-play.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CognitiveActivityService, CognitiveActivity } from '../../services/cognitive-activity.service';

interface GameState {
  activityId: number;
  currentPhase: 'MEMORIZE' | 'RECALL' | 'QUESTION' | 'COMPLETED' | 'ABANDONED';
  currentQuestionIndex: number;
  score: number;
  timeLeft: number;
  totalQuestions: number;
  answers: any[];
  startTime: Date;
  timerActive: boolean;
}

@Component({
  selector: 'app-activity-play',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './activity-play.html',
  styleUrls: ['./activity-play.css']
})
export class ActivityPlay implements OnInit, OnDestroy {
  activity: CognitiveActivity | null = null;
  loading = true;
  error = '';

  // Game state
  gameState: GameState = {
    activityId: 0,
    currentPhase: 'MEMORIZE',
    currentQuestionIndex: 0,
    score: 0,
    timeLeft: 30,
    totalQuestions: 3,
    answers: [],
    startTime: new Date(),
    timerActive: true
  };

  // Game content
  memoryWords: string[] = [];
  currentQuestion: any = null;
  questions: any[] = [];

  // User inputs
  userAnswer = '';
  selectedOption: number | null = null;
  sequenceAnswer = '';

  // Timer
  private timerInterval: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private activityService: CognitiveActivityService
  ) {}

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loadActivity(id);
  }

  ngOnDestroy() {
    this.stopTimer();
  }

  private stopTimer() {
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
    this.gameState.timerActive = false;
  }

  private startTimer() {
    if (!this.gameState.timerActive ||
      this.gameState.currentPhase === 'COMPLETED' ||
      this.gameState.currentPhase === 'ABANDONED') {
      return;
    }

    this.stopTimer();

    this.timerInterval = setInterval(() => {
      if (!this.gameState.timerActive ||
        this.gameState.currentPhase === 'COMPLETED' ||
        this.gameState.currentPhase === 'ABANDONED') {
        this.stopTimer();
        return;
      }

      this.gameState.timeLeft--;

      if (this.gameState.timeLeft <= 0) {
        this.handleTimeOut();
      }
    }, 1000);
  }

  loadActivity(id: number) {
    this.loading = true;
    this.activityService.getActivityById(id).subscribe({
      next: (data) => {
        this.activity = data;
        this.loading = false;
        this.initializeGame();
      },
      error: (err) => {
        this.error = 'Failed to load activity';
        this.loading = false;
        console.error('Error loading activity:', err);
      }
    });
  }

  initializeGame() {
    if (!this.activity) return;

    // Reset game state
    this.gameState = {
      activityId: this.activity.id || 0,
      currentPhase: 'MEMORIZE',
      currentQuestionIndex: 0,
      score: 0,
      timeLeft: 30,
      totalQuestions: 3,
      answers: [],
      startTime: new Date(),
      timerActive: true
    };

    try {
      const content = JSON.parse(this.activity.content || '{}');
      console.log('Game content:', content);

      switch (this.activity.type) {
        case 'MEMORY':
          this.initializeMemoryGame(content);
          break;
        case 'ATTENTION':
          this.initializeAttentionGame(content);
          break;
        case 'LOGIC':
          this.initializeLogicGame(content);
          break;
        default:
          this.error = 'Unknown game type';
      }

      this.startTimer();
    } catch (e) {
      console.error('Error parsing content:', e);
      this.error = 'Invalid game content';
    }
  }

  // 🧠 MEMORY GAME
  initializeMemoryGame(content: any) {
    console.log('Initializing MEMORY game');

    if (content.words) {
      this.memoryWords = content.words;
      this.gameState.totalQuestions = 1;
      this.gameState.currentPhase = 'MEMORIZE';
      this.gameState.timeLeft = 30;
    }
    else if (content.pairs) {
      this.questions = content.pairs;
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = this.activity?.timeLimit || 180;
      this.currentQuestion = this.questions[0];
    }
    else if (content.grid) {
      this.memoryWords = content.grid.flat();
      this.gameState.totalQuestions = 1;
      this.gameState.currentPhase = 'MEMORIZE';
      this.gameState.timeLeft = 45;
    }
    else {
      // Default memory game
      this.memoryWords = ['chat', 'chien', 'lapin', 'oiseau', 'poisson'];
      this.gameState.totalQuestions = 1;
      this.gameState.currentPhase = 'MEMORIZE';
      this.gameState.timeLeft = 30;
    }
  }

  // 👀 ATTENTION GAME
  initializeAttentionGame(content: any) {
    console.log('Initializing ATTENTION game');

    if (content.items) {
      this.questions = content.items;
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = this.activity?.timeLimit || 150;
      this.currentQuestion = this.questions[0];
    }
    else if (content.sequences) {
      this.questions = content.sequences.map((seq: any) => ({
        type: 'reverse',
        sequence: seq,
        correct: [...seq].reverse()
      }));
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = this.activity?.timeLimit || 240;
      this.currentQuestion = this.questions[0];
    }
    else {
      // Default attention game
      this.questions = [
        { word: 'ROUGE', color: 'blue', correct: 'blue' },
        { word: 'VERT', color: 'red', correct: 'red' },
        { word: 'BLEU', color: 'green', correct: 'green' }
      ];
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = 150;
      this.currentQuestion = this.questions[0];
    }
  }

  // 🔢 LOGIC GAME
  initializeLogicGame(content: any) {
    console.log('Initializing LOGIC game');

    if (content.sequences) {
      this.questions = content.sequences.map((seq: any) => {
        if (Array.isArray(seq)) {
          return {
            type: 'sequence',
            sequence: seq.slice(0, -1),
            correct: seq[seq.length - 1]
          };
        }
        return seq;
      });
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = this.activity?.timeLimit || 180;
      this.currentQuestion = this.questions[0];
    }
    else if (content.problems) {
      this.questions = content.problems;
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = this.activity?.timeLimit || 300;
      this.currentQuestion = this.questions[0];
    }
    else if (content.riddles) {
      this.questions = content.riddles;
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = this.activity?.timeLimit || 240;
      this.currentQuestion = this.questions[0];
    }
    else {
      // Default logic game
      this.questions = [
        { type: 'sequence', sequence: [2, 4, 6, 8], correct: 10 },
        { type: 'sequence', sequence: [5, 10, 15, 20], correct: 25 }
      ];
      this.gameState.totalQuestions = this.questions.length;
      this.gameState.currentPhase = 'QUESTION';
      this.gameState.timeLeft = 180;
      this.currentQuestion = this.questions[0];
    }
  }

  handleTimeOut() {
    console.log('Time out');
    this.stopTimer();

    if (this.gameState.currentPhase === 'MEMORIZE') {
      this.gameState.currentPhase = 'RECALL';
      this.gameState.timeLeft = this.activity?.timeLimit || 120;
      this.gameState.timerActive = true;
      this.startTimer();
    } else {
      this.moveToNextQuestion();
    }
  }

  startRecall() {
    console.log('Starting recall phase');
    this.stopTimer();
    this.gameState.currentPhase = 'RECALL';
    this.gameState.timeLeft = this.activity?.timeLimit || 120;
    this.gameState.currentQuestionIndex = 0;
    this.gameState.timerActive = true;
    this.startTimer();
  }

  submitAnswer() {
    if (!this.activity) return;

    console.log('Submitting answer for:', this.activity.type);

    switch (this.activity.type) {
      case 'MEMORY':
        this.submitMemoryAnswer();
        break;
      case 'ATTENTION':
        this.submitAttentionAnswer();
        break;
      case 'LOGIC':
        this.submitLogicAnswer();
        break;
    }
  }

  submitMemoryAnswer() {
    const userWords = this.userAnswer.toLowerCase().split(',').map(w => w.trim()).filter(w => w.length > 0);
    const correctWords = this.memoryWords.map(w => w.toLowerCase());

    const correctCount = userWords.filter(w => correctWords.includes(w)).length;
    const pointsEarned = Math.floor((correctCount / this.memoryWords.length) * 10);

    this.gameState.answers.push({
      type: 'memory',
      userAnswer: this.userAnswer,
      correctCount: correctCount,
      totalWords: this.memoryWords.length,
      pointsEarned: pointsEarned
    });

    this.gameState.score += pointsEarned;
    console.log('Memory score:', this.gameState.score);
    this.completeGame();
  }

  submitAttentionAnswer() {
    if (!this.currentQuestion) return;

    const selectedColor = this.selectedOption === 0 ? 'blue' :
      this.selectedOption === 1 ? 'red' :
        this.selectedOption === 2 ? 'green' : '';

    const isCorrect = selectedColor === this.currentQuestion.correct;
    const pointsEarned = isCorrect ? 10 : 0;

    this.gameState.answers.push({
      question: this.gameState.currentQuestionIndex,
      userAnswer: selectedColor,
      correct: isCorrect,
      pointsEarned: pointsEarned,
      correctAnswer: this.currentQuestion.correct
    });

    if (isCorrect) {
      this.gameState.score += pointsEarned;
    }

    console.log('Attention answer:', { selectedColor, isCorrect, score: this.gameState.score });
    this.moveToNextQuestion();
  }

  submitLogicAnswer() {
    if (!this.currentQuestion) return;

    let isCorrect = false;
    let pointsEarned = 0;

    if (this.currentQuestion.type === 'sequence') {
      const userNum = Number(this.sequenceAnswer);
      isCorrect = userNum === this.currentQuestion.correct;
      pointsEarned = isCorrect ? 10 : 0;
    } else {
      const userAnswerLower = this.userAnswer.toLowerCase().trim();
      const correctAnswer = this.currentQuestion.answer || this.currentQuestion.correct;
      isCorrect = userAnswerLower === correctAnswer.toString().toLowerCase().trim();
      pointsEarned = isCorrect ? 10 : 0;
    }

    this.gameState.answers.push({
      question: this.gameState.currentQuestionIndex,
      userAnswer: this.sequenceAnswer || this.userAnswer,
      correct: isCorrect,
      pointsEarned: pointsEarned,
      correctAnswer: this.currentQuestion.answer || this.currentQuestion.correct
    });

    if (isCorrect) {
      this.gameState.score += pointsEarned;
    }

    console.log('Logic answer:', { isCorrect, score: this.gameState.score });
    this.moveToNextQuestion();
  }

  moveToNextQuestion() {
    if (this.gameState.currentQuestionIndex < this.gameState.totalQuestions - 1) {
      this.gameState.currentQuestionIndex++;
      this.currentQuestion = this.questions[this.gameState.currentQuestionIndex];
      this.userAnswer = '';
      this.sequenceAnswer = '';
      this.selectedOption = null;

      this.stopTimer();
      this.gameState.timeLeft = this.activity?.timeLimit || 120;
      this.gameState.timerActive = true;
      this.startTimer();

      console.log('Moving to question:', this.gameState.currentQuestionIndex + 1);
    } else {
      this.completeGame();
    }
  }

  completeGame() {
    console.log('Game completed with score:', this.gameState.score);
    this.stopTimer();
    this.gameState.currentPhase = 'COMPLETED';
    this.gameState.timerActive = false;

    if (this.activity && this.activity.id) {
      const timeSpent = Math.floor((new Date().getTime() - this.gameState.startTime.getTime()) / 1000);
      this.activityService.completeActivity(
        this.activity.id,
        this.gameState.score,
        timeSpent
      ).subscribe({
        next: () => console.log('Results saved'),
        error: (err) => console.error('Error saving results', err)
      });
    }
  }

  abandonGame() {
    this.stopTimer();
    this.gameState.currentPhase = 'ABANDONED';
    this.gameState.timerActive = false;
  }

  restartGame() {
    this.stopTimer();
    this.userAnswer = '';
    this.sequenceAnswer = '';
    this.selectedOption = null;
    this.initializeGame();
  }

  viewResults() {
    this.router.navigate(['/activities', this.activity?.id]);
  }

  formatTime(seconds: number): string {
    if (seconds < 0) seconds = 0;
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  getProgressPercentage(): number {
    if (this.gameState.currentPhase === 'COMPLETED' || this.gameState.currentPhase === 'ABANDONED') {
      return 100;
    }
    if (this.gameState.currentPhase === 'MEMORIZE') {
      return 0;
    }
    return ((this.gameState.currentQuestionIndex) / this.gameState.totalQuestions) * 100;
  }

  getTypeIcon(type: string): string {
    const icons: Record<string, string> = {
      'MEMORY': '🧠',
      'ATTENTION': '👀',
      'LOGIC': '🔢'
    };
    return icons[type] || '📝';
  }

  getStatusMessage(): string {
    if (this.gameState.currentPhase === 'MEMORIZE') {
      return `Memorize... Time left: ${this.formatTime(this.gameState.timeLeft)}`;
    }
    if (this.gameState.currentPhase === 'RECALL' || this.gameState.currentPhase === 'QUESTION') {
      return `Question ${this.gameState.currentQuestionIndex + 1}/${this.gameState.totalQuestions}`;
    }
    if (this.gameState.currentPhase === 'COMPLETED') {
      return `Game Complete! Score: ${this.gameState.score}`;
    }
    if (this.gameState.currentPhase === 'ABANDONED') {
      return `Game Abandoned`;
    }
    return '';
  }

  getCorrectAnswersCount(): number {
    return this.gameState.answers.filter(a => a.correct).length;
  }

  getTotalPoints(): number {
    return this.gameState.score;
  }

  getTimeSpent(): string {
    if (!this.gameState.startTime) return '0:00';
    const timeSpentSeconds = Math.floor((new Date().getTime() - this.gameState.startTime.getTime()) / 1000);
    return this.formatTime(timeSpentSeconds);
  }
}
