# ネットワークプログラミング課題（C, InfiniBand） / Network Programming Project

## 概要（日本語）

このプロジェクトは、エルサレム・ヘブライ大学のネットワークプログラミングに関する授業の総まとめとして実装した課題です。

C 言語と InfiniBand (RDMA) の low-level API（verbs）を用いて、  
クライアント・サーバ型の通信を行う小さなプログラムを実装しています。

主なねらいは以下の通りです。

- ソケットレベルよりもさらに低いレイヤでの通信の仕組みを理解すること  
- Queue Pair（キュー・ペア）や Completion Queue（コンプリーションキュー）など RDMA 特有の概念に触れること  
- メモリバッファの割り当て・登録・送受信・解放までを自前で管理すること  
- 講義で学んだネットワークの概念を、実際のコードとして動かして確認すること

プロジェクトは、簡単なリクエスト／レスポンス型のやり取りを行うサーバとクライアントで構成されています。

---

## 何をしているプログラムか（日本語）

- サーバ側は、InfiniBand 上で接続を初期化し、クライアントからのリクエストを受け取ります。  
- クライアント側は、接続を確立したあと、指定回数だけメッセージを送信し、サーバからの応答を受け取ります。  
- 通信は RDMA verbs を用いて行われ、  
  - メモリを登録する  
  - 送信／受信ワークリクエストをポストする  
  - Completion Queue から完了イベントをポーリングする  
  といった処理を通して、データの送受信が行われます。

課題全体としては、講義で扱ったネットワークの概念（レイテンシ、スループット、信頼性など）を、  
実際に InfiniBand 環境で測定・体験しながら理解を深めることが目的でした。

---

## 技術要素（日本語）

- 言語: C  
- 通信: InfiniBand / RDMA (verbs API)  
- 主な要素:
  - Queue Pair, Completion Queue の初期化
  - メモリバッファの割り当て・登録
  - send / receive ワークリクエストのポスト
  - 完了イベントのポーリングとエラーハンドリング

この課題を通して、より高レベルなライブラリやフレームワークの裏側で何が起きているのかを体感し、  
低レベルなコードの管理やデバッグの難しさ・おもしろさを学びました。

---

## Overview (English)

This project was created as a final practical assignment for a Network Programming course at the Hebrew University of Jerusalem.

It is a C project using InfiniBand (RDMA) low-level APIs (verbs) to implement a small client–server style program.  
The main goals were:

- To understand how communication works at a lower level than regular sockets  
- To work with RDMA-specific concepts such as Queue Pairs (QP) and Completion Queues (CQ)  
- To manually manage memory buffers for sending and receiving data  
- To connect the theory from the lectures with a working, measurable program

The project consists of a server and a client that exchange simple request/response messages over InfiniBand.

---

## What the program does (English)

- The server initializes an InfiniBand connection and waits for incoming requests from a client.  
- The client connects to the server, sends a series of messages, and waits for the server’s responses.  
- Communication is implemented using RDMA verbs:
  - Registering memory regions  
  - Posting send/receive work requests  
  - Polling completion queues for finished operations  
  - Handling errors and teardown

In the course, we used this type of program to experiment with latency, throughput and other performance aspects of the network, and to better understand what is happening under the hood of higher-level networking libraries.

---

## Technologies (English)

- Language: C  
- Transport: InfiniBand / RDMA (verbs API)  
- Key elements:
  - Initialization of queue pairs and completion queues  
  - Allocation and registration of memory buffers  
  - Posting send and receive operations  
  - Polling for completions and handling failures

This project demonstrates my ability to work with low-level networking APIs, reason about performance, and manage memory and resources carefully in C.
