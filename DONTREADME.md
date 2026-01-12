## Goals

### Likelihood

> Are the questions guaranteed to be grammatical?

No. People often speak ungrammatically. The goal is to capture the questions that people likely ask in small talk.

### Uniqueness

> Can the pipeline deduplicate questions?

Yes. The pipeline uses cosine similarity and connected components to group related questions and picks the one with the highest likelihood score from each group.

### Quantity

> How many questions does the pipeline generate?

The goal is at least 1,000 questions.

### Cost

> What is the budget for generating and ranking the questions?

The goal is to stay under $1,000.
