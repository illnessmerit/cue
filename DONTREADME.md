## Goals

### Likelihood

> Does `cue` parse questions from a corpus?

No. There are issues with conversational datasets:

- Most datasets are too small. In a limited sample, common patterns are easily missed while outliers may be overrepresented.

- The settings are often artificial. SWITCHBOARD, for instance, features strangers who were told to discuss assigned topics over the phone.

Instead, the pipeline mines the model's probability distribution.

> Does `cue` use an instruction-tuned model?

No. Instruction-tuned models are fine-tuned to be helpful. That fine-tuning warps their probability distribution toward how an assistant should act rather than how humans talk.

> Are the questions guaranteed to be grammatical?

No. People often speak ungrammatically. The goal is to capture the questions that people likely ask in conversation.

> Does an LLM as a judge assign the likelihood scores?

No. The likelihood scores are the log probabilities the base model calculates during the search.

### Uniqueness

> Can the pipeline deduplicate questions?

Yes. The pipeline uses cosine similarity and connected components to group related questions and picks the one with the highest likelihood score from each group.

### Quantity

> How many questions does the pipeline generate?

The goal is at least 1,000 questions.

### Cost

> What is the budget for generating and ranking the questions?

The goal is to stay under $1,000.
