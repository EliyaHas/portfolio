# 週次レポート自動生成RPA（Power Automate Desktop + Excel）

## 概要
不動産会社向けの「週次進捗レポート」を、FileMakerの書き出しデータから自動生成するRPAを開発しています。  
主に **Microsoft Power Automate Desktop (PAD)** を使い、Excelテンプレートへデータ反映・集計・PDF出力までを一連で実行できるようにしています。

## 背景と目的
週次レポート作成は、手作業だと「データ抽出→整形→集計→体裁調整→PDF化」の工程が多く、ミスや工数が発生しやすい作業です。  
本プロジェクトでは、手順を標準化しつつ、案件ごとの列構成の違いにも耐えられる形で自動化することで、安定した納品と作業時間の削減を目指しています。

## 主な機能
- FileMakerの書き出しデータ（Excel/CSV）を読み込み、顧客データを整形
- 管理コードでグルーピングし、会社単位で成果物を出力
- 「開示対象列（管理データで〇指定）」のみを抽出して顧客一覧へ反映
- 反響日から **反響月（yyyyMM）** を生成（PAD側で値として作成）
- Excelテンプレート（表）へ流し込み、集計表を自動更新
- 条件に応じた行の表示/非表示（PowerShell + Excel COM）でPDFレイアウトを調整
- 指定シートをPDFとして出力（PowerShell + `ExportAsFixedFormat`）

## 設計方針（工夫した点）
- **Excelへの行単位書き込みを最小化**し、データ整形はPADのDataTable側で実施
- テンプレートを壊さないため、列参照は **ヘッダー名ベース（XMATCH/INDEX など）** を基本に設計
- 月次列は「常に直近6か月」を表示し、実行時にヘッダーを **値として固定（freeze）** して再現性を確保
- 案件ごとに列が増減する前提のため、出力対象列（〇）は管理データで可変に対応
- PDFはサンプル形式に合わせ、店舗数に応じてブロック数/ページ構成が変動する形を想定

## 技術スタック
- Microsoft Power Automate Desktop (PAD)
- Excel（テンプレート、数式、印刷設定）
- PowerShell（Excel COM：行の非表示、PDF出力）
- （必要に応じて）設定ファイルによる列名マッピング/変更耐性の向上

## 制約・注意
本リポジトリでは、実案件のデータやテンプレート、社内固有の情報は含めていません。  
設計・考え方・実装のポイントを中心に整理しています。

---

# Weekly Report Automation RPA (Power Automate Desktop + Excel)

## Overview
I’m building an RPA pipeline that generates weekly progress deliverables for real-estate companies from FileMaker export data.  
The automation is mainly implemented in **Microsoft Power Automate Desktop (PAD)** and produces Excel outputs and PDF reports based on an Excel template.

## Goals
Manual weekly reporting involves many repetitive steps (export → clean → aggregate → format → PDF).  
This project aims to standardize the workflow, reduce human errors, and handle varying column configurations safely.

## Key Features
- Load FileMaker export data (Excel/CSV) and normalize customer rows
- Group outputs by management codes and generate deliverables per company
- Select only “disclosure columns” controlled by a separate management table
- Create **Inquiry Month (yyyyMM)** from dates inside PAD (as values, not formulas)
- Fill an Excel template and let summary tables update automatically
- Dynamically hide/unhide layout rows via PowerShell (Excel COM) for clean PDF formatting
- Export specific sheets to PDF using `ExportAsFixedFormat`

## Design Highlights
- Minimize row-by-row Excel writes; do heavy transformations in PAD DataTables
- Prefer header-name-based references (e.g., INDEX/XMATCH) to reduce template breakage
- Freeze the “last 6 months” headers as values at runtime for reproducibility
- Support variable output columns defined externally (disclosure control)
- PDF layout adapts to the number of stores (variable blocks/pages)

## Tech Stack
- Microsoft Power Automate Desktop (PAD)
- Excel (template, formulas, print settings)
- PowerShell (Excel COM: row visibility + PDF export)

## Notes
This repository does not include any real customer data, proprietary templates, or internal company-specific identifiers.  
It focuses on the automation approach and implementation patterns.