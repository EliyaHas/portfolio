# University Projects Portfolio / 大学プロジェクト・ポートフォリオ

## 概要（日本語）

このリポジトリは、エルサレム・ヘブライ大学コンピュータサイエンス学科での授業プロジェクトをまとめたポートフォリオです。  
オブジェクト指向プログラミング（Java）とネットワークプログラミング（C, InfiniBand）という、性質の異なる2つのテーマの課題を収録しています。

- `oop_project/`  
  Java を用いたオブジェクト指向プログラミングの課題です。  
  無限に続く 2D の世界をシミュレーションするゲーム風アプリケーションで、Perlin Noise による地形生成、木や葉、エネルギーバー、トラッキングする Sun Halo などを、クラス設計・継承・ファクトリなどの OOP 概念を用いて実装しています。

- `network_project/`  
  C 言語と InfiniBand (RDMA) を用いたネットワークプログラミングの課題です。  
  クライアント／サーバ間通信を自前で扱い、キュー・ペアやコンプリーションキューなどの概念を使って、低レベルなメッセージ送受信と簡単なリクエスト処理を実装しています。

※現職で取り組んでいる OCR／RPA／業務自動化プロジェクトについては、業務上の理由でソースコードは公開できないため、今後概要のみ別ファイルで追記予定です。

---

## Overview (English)

This repository is a portfolio of university course projects from my Computer Science degree at the Hebrew University of Jerusalem.  
It contains two projects in different areas: object-oriented programming in Java, and network programming in C using InfiniBand (RDMA).

- `oop_project/`  
  A Java project for the Object-Oriented Programming course.  
  It implements a small game-like 2D world simulation that continues “infinitely”, using Perlin noise for terrain generation, trees and leaves, an energy bar, and a Sun Halo that tracks the sun. The focus is on class design, inheritance, and factory-style object creation.

- `network_project/`  
  A C / InfiniBand network programming project.  
  It implements client–server communication using low-level RDMA verbs: setting up queue pairs, posting send/receive operations, and handling completion events to exchange messages and simple requests between processes.

My current work (OCR / RPA / operations automation in Japan) involves cloud OCR, data normalization, and RPA flows, but due to confidentiality I cannot publish that source code. I plan to add a description-only document later.
