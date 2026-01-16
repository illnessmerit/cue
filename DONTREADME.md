## Goals

### Likelihood

> Does an LLM as a judge assign the likelihood scores?

No. Using an LLM as a judge to rank the list isn't straightforward. A full pairwise comparison would be quadratic and expensive. A faster method like Elo has its own problems, like figuring out when the ratings have stabilized.

The likelihood scores are the log probabilities a model calculates during search.

> Are the likelihood scores guaranteed to match real-world frequencies?

No. The scores come from the model's distribution, not real-world counts. They're unlikely to match reality perfectly.

But they help favor natural phrasing over awkward or rare alternatives.

### Uniqueness

> Can the pipeline deduplicate sentences?

Yes. The pipeline uses cosine similarity and connected components to group related sentences and picks the one with the highest likelihood score from each group.

### Quantity

> How many sentences does the pipeline generate?

The goal is at least 1,000 sentences.

### Cost

> What is the budget for generating and ranking the sentences?

The goal is to stay under $1,000.

## Search

> Does `cue` parse sentences from a corpus?

No. There are issues with conversational datasets:

- Most datasets are too small. In a limited sample, common patterns are easily missed while outliers may be overrepresented.

- The settings are often artificial. SWITCHBOARD, for instance, features strangers who were told to discuss assigned topics over the phone.

Instead, `cue` mines the model's probability distribution.

> Does `cue` use an instruction-tuned model to find candidates?

No. Instruction-tuned models are fine-tuned to be helpful. That fine-tuning warps their probability distribution toward how an assistant should act rather than how humans talk.

Instead, `cue` uses a base model.

> Does `cue` fine-tune a base model?

No. Spontaneous conversation data is hard to find. Fine-tuning on artificially constructed data risks biasing the model toward the patterns of forced settings.

> Does `cue` use a base model with a prompt?

Yes. It uses the prompt `She's like, "`. There are a few reasons:

- Women outnumber men in the US, making `she` a more sensible default than `he`.

- The phrasing `She's like` primes the model for a more casual completion than `She says`.

- The opening quote primes the model for spoken dialogue.

> Are the sentences guaranteed to be grammatical?

No. People often speak ungrammatically. The goal is to capture the sentences that people likely ask in conversation.
