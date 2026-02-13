# University Projects Portfolio / 大学プロジェクト・ポートフォリオ

## 概要（日本語）

このリポジトリは、エルサレム・ヘブライ大学コンピュータサイエンス学科での授業プロジェクトと、現在日本で取り組んでいる業務自動化プロジェクトの概要をまとめたポートフォリオです。

現在、以下の 3 つの内容を公開しています。

- `oop_project/`  
  Java を用いたオブジェクト指向プログラミングの課題です。  
  無限に続く 2D の世界をシミュレーションするゲーム風アプリケーションで、Perlin Noise による地形生成、木や葉、エネルギーバー、太陽を追従する Sun Halo などを、クラス設計・継承・ファクトリパターンなどの OOP 概念を用いて実装しています。詳細はフォルダ内の `README.md` を参照してください。

- `network_project/`  
  C 言語と InfiniBand (RDMA) を用いたネットワークプログラミングの課題です。  
  クライアント／サーバ間でメッセージをやり取りする小さなプログラムで、Queue Pair や Completion Queue、メモリ登録など、低レベルなネットワーク API を使用しています。詳細はフォルダ内の `README.md` を参照してください。

- `work_rpa_project/`  
  現職で取り組んでいる「週次レポート自動生成 RPA」の設計・考え方を整理したものです。  
  Microsoft Power Automate Desktop と Excel・PowerShell を組み合わせて、不動産会社向けの FileMaker 書き出しデータから週次進捗レポート（Excel／PDF）を自動生成するフローを設計しています。実データや社内テンプレートは含めず、アプローチと工夫したポイントのみを記載しています。

今後、業務プロジェクトの説明や小規模な個人開発なども追加していく予定です。

---

## Overview (English)

This repository is a portfolio that combines university course projects from my Computer Science degree at the Hebrew University of Jerusalem and a description of my current automation work in Japan.

Currently it contains three main parts:

- `oop_project/`  
  A Java project for the Object-Oriented Programming course.  
  It implements an “infinite” 2D world simulation using Perlin noise for terrain, trees and leaves, an energy bar, and a Sun Halo that tracks the sun. The focus is on class design, inheritance, and factory-style object creation. See the `README.md` inside the folder for details.

- `network_project/`  
  A C / InfiniBand (RDMA) network programming project.  
  It implements a small client–server style program that exchanges messages using low-level RDMA verbs (queue pairs, completion queues, memory registration, etc.). See the folder `README.md` for more details.

- `work_rpa_project/`  
  A description of my current work on weekly report automation using RPA.  
  The project uses Microsoft Power Automate Desktop, Excel, and PowerShell to generate weekly progress reports (Excel/PDF) for real-estate companies from FileMaker export data. No real customer data or internal templates are included; the focus is on the automation design and patterns used.

I plan to extend this repository over time with more explanations of my work projects and small personal experiments.
