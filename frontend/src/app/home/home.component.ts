import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Brain3dComponent } from '../brain3d/brain3d.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, Brain3dComponent],
  template: `
    <div class="home-container">
      <!-- Hero Section -->
      <section class="hero-section">
        <div class="hero-background">
          <div class="neural-network"></div>
          <div class="particles"></div>
        </div>
        
        <div class="hero-content">
          <div class="hero-text">
            <div class="badge">
              <span class="badge-icon">🧬</span>
              <span>AI-Powered Cognitive Health Platform</span>
            </div>
            
            <h1 class="hero-title">
              <span class="title-line">Welcome to</span>
              <span class="title-brand">COGNIVITA</span>
            </h1>
            
            <p class="hero-description">
              Advanced AI-driven platform for early detection and monitoring of cognitive decline. 
              Combining cutting-edge neural networks with comprehensive cognitive assessments.
            </p>
            
            <div class="hero-stats">
              <div class="stat-item">
                <span class="stat-value">98%</span>
                <span class="stat-label">Accuracy Rate</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">3D</span>
                <span class="stat-label">Brain Visualization</span>
              </div>
              <div class="stat-item">
                <span class="stat-value">AI</span>
                <span class="stat-label">Deep Learning</span>
              </div>
            </div>
            
            <div class="hero-actions">
              <a routerLink="/mmse" class="btn-primary">
                <span class="btn-icon">📋</span>
                <span>Start MMSE Test</span>
              </a>
              <a routerLink="/cnn" class="btn-secondary">
                <span class="btn-icon">🤖</span>
                <span>AI Brain Scanner</span>
              </a>
            </div>
          </div>
          
          <div class="hero-brain">
            <div class="brain-container">
              <div class="brain-glow"></div>
              <app-brain3d [diagnosis]="'normal'"></app-brain3d>
              <div class="brain-label">
                <span class="pulse-dot"></span>
                Interactive 3D Brain Model
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="features-section">
        <h2 class="section-title">
          <span class="title-icon">✨</span>
          Our Cognitive Assessment Tools
        </h2>
        
        <div class="features-grid">
          <div class="feature-card" routerLink="/mmse">
            <div class="feature-icon">📋</div>
            <h3 class="feature-title">MMSE Test</h3>
            <p class="feature-desc">
              Mini-Mental State Examination - A standardized cognitive assessment 
              evaluating orientation, memory, attention, and language skills.
            </p>
            <div class="feature-arrow">→</div>
          </div>
          
          <div class="feature-card" routerLink="/cnn">
            <div class="feature-icon">🤖</div>
            <h3 class="feature-title">AI Brain Scanner</h3>
            <p class="feature-desc">
              Advanced CNN-based analysis of brain MRI scans to detect early signs 
              of cognitive impairment with high accuracy.
            </p>
            <div class="feature-arrow">→</div>
          </div>
          
          <div class="feature-card highlight">
            <div class="feature-icon">🧠</div>
            <h3 class="feature-title">3D Visualization</h3>
            <p class="feature-desc">
              Interactive 3D brain model visualization showing affected regions 
              and helping understand diagnosis results.
            </p>
            <div class="feature-tag">Interactive</div>
          </div>
        </div>
      </section>

      <!-- How It Works Section -->
      <section class="how-it-works">
        <h2 class="section-title">
          <span class="title-icon">🔬</span>
          How It Works
        </h2>
        
        <div class="steps-container">
          <div class="step">
            <div class="step-number">01</div>
            <div class="step-content">
              <h3>Upload or Test</h3>
              <p>Upload brain MRI scans or take the MMSE cognitive assessment test</p>
            </div>
          </div>
          
          <div class="step-connector"></div>
          
          <div class="step">
            <div class="step-number">02</div>
            <div class="step-content">
              <h3>AI Analysis</h3>
              <p>Our advanced neural network analyzes the data in real-time</p>
            </div>
          </div>
          
          <div class="step-connector"></div>
          
          <div class="step">
            <div class="step-number">03</div>
            <div class="step-content">
              <h3>Get Results</h3>
              <p>Receive detailed results with 3D visualization and recommendations</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Footer -->
      <footer class="footer">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="footer-logo">🧠</span>
            <span class="footer-name">COGNIVITA</span>
          </div>
          <p class="footer-text">
            Advanced Cognitive Health Assessment Platform
          </p>
          <p class="footer-disclaimer">
            This tool is for screening purposes only and does not replace professional medical diagnosis.
          </p>
        </div>
      </footer>
    </div>
  `,
  styles: [`
    .home-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #0a0a1a 0%, #1a1a3a 50%, #0a0a2a 100%);
      overflow-x: hidden;
      padding-top: 70px;
    }

    /* Hero Section */
    .hero-section {
      position: relative;
      min-height: calc(100vh - 70px);
      display: flex;
      align-items: center;
      padding: 40px 20px;
      overflow: hidden;
    }

    .hero-background {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
    }

    .neural-network {
      position: absolute;
      width: 100%;
      height: 100%;
      background-image: 
        radial-gradient(circle at 20% 30%, rgba(0, 255, 255, 0.1) 0%, transparent 50%),
        radial-gradient(circle at 80% 70%, rgba(255, 0, 255, 0.1) 0%, transparent 50%),
        radial-gradient(circle at 50% 50%, rgba(100, 100, 255, 0.05) 0%, transparent 70%);
      animation: pulse 8s ease-in-out infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 0.5; transform: scale(1); }
      50% { opacity: 1; transform: scale(1.05); }
    }

    .hero-content {
      max-width: 1400px;
      margin: 0 auto;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 60px;
      align-items: center;
      position: relative;
      z-index: 1;
    }

    .hero-text {
      padding: 20px;
    }

    .badge {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      background: rgba(0, 255, 255, 0.1);
      border: 1px solid rgba(0, 255, 255, 0.3);
      padding: 8px 16px;
      border-radius: 50px;
      font-size: 0.85rem;
      color: #00ffff;
      margin-bottom: 24px;
      animation: glow 2s ease-in-out infinite;
    }

    @keyframes glow {
      0%, 100% { box-shadow: 0 0 10px rgba(0, 255, 255, 0.3); }
      50% { box-shadow: 0 0 20px rgba(0, 255, 255, 0.6); }
    }

    .badge-icon {
      font-size: 1rem;
    }

    .hero-title {
      margin: 0 0 24px;
      line-height: 1.1;
    }

    .title-line {
      display: block;
      font-size: 1.5rem;
      font-weight: 400;
      color: #b0e0ff;
      margin-bottom: 8px;
    }

    .title-brand {
      display: block;
      font-size: 4rem;
      font-weight: 900;
      font-family: 'Orbitron', sans-serif;
      background: linear-gradient(135deg, #00ffff 0%, #ff00ff 50%, #00ccff 100%);
      background-size: 200% 200%;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      animation: textShimmer 3s ease-in-out infinite;
      letter-spacing: 4px;
    }

    @keyframes textShimmer {
      0%, 100% { background-position: 0% 50%; }
      50% { background-position: 100% 50%; }
    }

    .hero-description {
      font-size: 1.1rem;
      color: #93c5fd;
      line-height: 1.8;
      margin-bottom: 32px;
    }

    .hero-stats {
      display: flex;
      gap: 40px;
      margin-bottom: 40px;
    }

    .stat-item {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .stat-value {
      font-size: 2rem;
      font-weight: 800;
      color: #00ffff;
      font-family: 'Orbitron', sans-serif;
    }

    .stat-label {
      font-size: 0.85rem;
      color: #93c5fd;
      opacity: 0.8;
    }

    .hero-actions {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
    }

    .btn-primary, .btn-secondary {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      padding: 16px 32px;
      border-radius: 12px;
      font-size: 1rem;
      font-weight: 600;
      text-decoration: none;
      transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      cursor: pointer;
    }

    .btn-primary {
      background: linear-gradient(135deg, #00ffff 0%, #00ccff 100%);
      color: #0a0a1a;
      box-shadow: 0 4px 20px rgba(0, 255, 255, 0.4);
    }

    .btn-primary:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 30px rgba(0, 255, 255, 0.6);
    }

    .btn-secondary {
      background: rgba(255, 0, 255, 0.1);
      border: 2px solid rgba(255, 0, 255, 0.5);
      color: #ff88ff;
    }

    .btn-secondary:hover {
      background: rgba(255, 0, 255, 0.2);
      transform: translateY(-3px);
      box-shadow: 0 8px 30px rgba(255, 0, 255, 0.4);
    }

    .btn-icon {
      font-size: 1.2rem;
    }

    /* Brain Container */
    .hero-brain {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .brain-container {
      position: relative;
      width: 500px;
      height: 500px;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .brain-glow {
      position: absolute;
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, rgba(0, 255, 255, 0.3) 0%, transparent 70%);
      border-radius: 50%;
      animation: brainGlow 3s ease-in-out infinite;
    }

    @keyframes brainGlow {
      0%, 100% { transform: scale(1); opacity: 0.5; }
      50% { transform: scale(1.1); opacity: 0.8; }
    }

    .brain-container app-brain3d {
      width: 100%;
      height: 100%;
      position: relative;
      z-index: 1;
    }

    .brain-label {
      position: absolute;
      bottom: 20px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      align-items: center;
      gap: 8px;
      background: rgba(0, 0, 0, 0.6);
      backdrop-filter: blur(10px);
      padding: 8px 16px;
      border-radius: 20px;
      font-size: 0.85rem;
      color: #b0e0ff;
      white-space: nowrap;
    }

    .pulse-dot {
      width: 8px;
      height: 8px;
      background: #00ff88;
      border-radius: 50%;
      animation: pulseDot 1.5s ease-in-out infinite;
    }

    @keyframes pulseDot {
      0%, 100% { opacity: 1; transform: scale(1); }
      50% { opacity: 0.5; transform: scale(1.5); }
    }

    /* Features Section */
    .features-section {
      padding: 80px 20px;
      max-width: 1200px;
      margin: 0 auto;
    }

    .section-title {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      font-size: 2rem;
      font-weight: 700;
      color: #ffffff;
      margin-bottom: 48px;
      text-align: center;
    }

    .title-icon {
      font-size: 1.5rem;
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 24px;
    }

    .feature-card {
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(0, 255, 255, 0.2);
      border-radius: 20px;
      padding: 32px;
      transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      cursor: pointer;
      position: relative;
      overflow: hidden;
    }

    .feature-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, #00ffff, #ff00ff);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .feature-card:hover {
      transform: translateY(-8px);
      background: rgba(0, 255, 255, 0.05);
      border-color: rgba(0, 255, 255, 0.5);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), 0 0 30px rgba(0, 255, 255, 0.2);
    }

    .feature-card:hover::before {
      opacity: 1;
    }

    .feature-card.highlight {
      border-color: rgba(255, 0, 255, 0.4);
    }

    .feature-card.highlight:hover {
      border-color: rgba(255, 0, 255, 0.6);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3), 0 0 30px rgba(255, 0, 255, 0.3);
    }

    .feature-icon {
      font-size: 3rem;
      margin-bottom: 20px;
      filter: drop-shadow(0 0 8px rgba(0, 255, 255, 0.5));
    }

    .feature-title {
      font-size: 1.3rem;
      font-weight: 700;
      color: #ffffff;
      margin-bottom: 12px;
    }

    .feature-desc {
      font-size: 0.95rem;
      color: #93c5fd;
      line-height: 1.6;
      margin-bottom: 16px;
    }

    .feature-arrow {
      font-size: 1.5rem;
      color: #00ffff;
      opacity: 0;
      transform: translateX(-10px);
      transition: all 0.3s ease;
    }

    .feature-card:hover .feature-arrow {
      opacity: 1;
      transform: translateX(0);
    }

    .feature-tag {
      position: absolute;
      top: 16px;
      right: 16px;
      background: linear-gradient(135deg, #ff00ff, #ff66ff);
      color: white;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: 600;
    }

    /* How It Works */
    .how-it-works {
      padding: 80px 20px;
      background: rgba(0, 0, 0, 0.3);
    }

    .steps-container {
      max-width: 1000px;
      margin: 0 auto;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 20px;
    }

    .step {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      flex: 1;
      max-width: 250px;
    }

    .step-number {
      font-size: 3rem;
      font-weight: 900;
      font-family: 'Orbitron', sans-serif;
      background: linear-gradient(135deg, #00ffff, #ff00ff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 16px;
    }

    .step-content h3 {
      font-size: 1.2rem;
      font-weight: 700;
      color: #ffffff;
      margin-bottom: 8px;
    }

    .step-content p {
      font-size: 0.9rem;
      color: #93c5fd;
      line-height: 1.5;
    }

    .step-connector {
      width: 60px;
      height: 3px;
      background: linear-gradient(90deg, #00ffff, #ff00ff);
      border-radius: 2px;
      opacity: 0.5;
    }

    /* Footer */
    .footer {
      padding: 60px 20px;
      border-top: 1px solid rgba(0, 255, 255, 0.2);
    }

    .footer-content {
      max-width: 600px;
      margin: 0 auto;
      text-align: center;
    }

    .footer-brand {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      margin-bottom: 16px;
    }

    .footer-logo {
      font-size: 2rem;
    }

    .footer-name {
      font-size: 1.5rem;
      font-weight: 800;
      font-family: 'Orbitron', sans-serif;
      background: linear-gradient(135deg, #00ffff, #ff00ff);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .footer-text {
      color: #b0e0ff;
      margin-bottom: 16px;
    }

    .footer-disclaimer {
      font-size: 0.85rem;
      color: #6b7280;
      font-style: italic;
    }

    /* Responsive */
    @media (max-width: 1024px) {
      .hero-content {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .hero-text {
        order: 2;
      }

      .hero-brain {
        order: 1;
      }

      .brain-container {
        width: 350px;
        height: 350px;
      }

      .hero-stats {
        justify-content: center;
      }

      .hero-actions {
        justify-content: center;
      }

      .features-grid {
        grid-template-columns: 1fr;
        max-width: 500px;
        margin: 0 auto;
      }

      .steps-container {
        flex-direction: column;
        gap: 40px;
      }

      .step-connector {
        width: 3px;
        height: 40px;
      }
    }

    @media (max-width: 640px) {
      .title-brand {
        font-size: 2.5rem;
        letter-spacing: 2px;
      }

      .hero-stats {
        flex-direction: column;
        gap: 20px;
      }

      .brain-container {
        width: 280px;
        height: 280px;
      }

      .btn-primary, .btn-secondary {
        width: 100%;
        justify-content: center;
      }
    }
  `]
})
export class HomeComponent {}
