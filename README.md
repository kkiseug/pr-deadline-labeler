# PR Deadline Labeler

PR 코드리뷰 마감일을 기반으로 라벨을 자동으로 부착하는 GitHub Action입니다.

---

## 동작 방식

### 트리거별 동작

| 트리거 | 동작 |
|--------|------|
| PR 생성 (`pull_request: opened`) | 생성일 기준으로 `D-N` 라벨 부착 |
| 매일 자정 (`schedule`) | 열린 모든 PR의 라벨을 하루씩 업데이트 |
| 리뷰 등록 (`pull_request_review: submitted`) | 리뷰 등록일 기준으로 `D-N` 라벨 리셋 |

### 라벨 전환 흐름

`deadline-days: 5` 설정 시:

```
생성일   D+1    D+2    D+3    D+4    D+5    D+6
 D-5  → D-4 → D-3 → D-2 → D-1 → D-Day → OVER-DUE
```

### 특이 사항

- 라벨이 존재하지 않으면 **자동으로 생성**합니다.
- `OVER-DUE` 상태의 PR은 **더 이상 업데이트하지 않습니다.**
- 리뷰가 등록되면 마감일이 **리뷰 등록일 기준으로 리셋**됩니다.

---

## 사용법

### 1. 워크플로우 파일 추가

`.github/workflows/deadline-labeler.yml` 파일을 생성합니다.

```yaml
name: PR Deadline Labeler

on:
  pull_request:
    types: [opened]
  schedule:
    - cron: '0 0 * * *'
  pull_request_review:
    types: [submitted]

env:
  DEADLINE_DAYS: 5  # 마감일 설정 (기본값: 2)

jobs:
  label:
    runs-on: ubuntu-latest
    permissions:
      issues: write
      pull-requests: write

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Attach label on PR opened
        if: github.event_name == 'pull_request'
        uses: kkiseug/pr-deadline-labeler@v1
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          base-date: ${{ github.event.pull_request.created_at }}
          pr-number: ${{ github.event.pull_request.number }}
          deadline-days: ${{ env.DEADLINE_DAYS }}

      - name: Update labels on schedule
        if: github.event_name == 'schedule'
        uses: kkiseug/pr-deadline-labeler@v1
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          base-date: ${{ github.event.pull_request.created_at }}
          deadline-days: ${{ env.DEADLINE_DAYS }}

      - name: Reset label on review submitted
        if: github.event_name == 'pull_request_review'
        uses: kkiseug/pr-deadline-labeler@v1
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          base-date: ${{ github.event.review.submitted_at }}
          pr-number: ${{ github.event.pull_request.number }}
          deadline-days: ${{ env.DEADLINE_DAYS }}
```

### 2. 권한 설정

워크플로우에 아래 권한이 필요합니다.

```yaml
permissions:
  issues: write
  pull-requests: write
```

---

## Inputs

| Input | 필수 여부 | 기본값 | 설명 |
|-------|----------|--------|------|
| `github-token` | ✅ 필수 | - | GitHub 접근을 위한 토큰 |
| `base-date` | ✅ 필수 | - | 라벨 계산 기준일 (PR 생성일 또는 리뷰 등록일) |
| `pr-number` | ❌ 선택 | - | 업데이트할 PR 번호 (없으면 전체 PR 업데이트) |
| `deadline-days` | ❌ 선택 | `2` | 코드리뷰 최대 마감일 |
